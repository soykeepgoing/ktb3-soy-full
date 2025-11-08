package com.soy.springcommunity.service;

import com.soy.springcommunity.dto.*;
import com.soy.springcommunity.entity.UserDetail;
import com.soy.springcommunity.entity.Users;
import com.soy.springcommunity.exception.UsersException;
import com.soy.springcommunity.repository.users.UsersDetailRepository;
import com.soy.springcommunity.repository.users.UsersRepository;
import com.soy.springcommunity.utils.ConstantUtil;
import com.soy.springcommunity.utils.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UsersService {
    private UsersRepository usersRepository;
    private UsersDetailRepository usersDetailRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        UsersDetailRepository usersDetailRepository) {
        this.usersRepository = usersRepository;
        this.usersDetailRepository = usersDetailRepository;
    }

    private boolean isEmailExist(String email) {
        return usersRepository.findByEmailAndIsDeletedFalse(email).isPresent();
    }

    private boolean isNicknameExist(String nickname) {
        return usersRepository.findByNickname(nickname).isPresent();
    }

    private String base64ToUrl(String base64Data) throws IOException {
        // 이미지 데이터 변환
        String[] parts = base64Data.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];
        byte[] decoded = Base64.getDecoder().decode(imageData);

        // 파일 로컬 서버에 저장
        String fileName = UUID.randomUUID() + ".png";
        Path path = Paths.get("uploads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, decoded);

        // url 생성
        String fileUrl = "http://localhost:8080/uploads/" + fileName;
        return fileUrl;
    }

    private String checkAndSetProfileImage(String profileImgUrl) throws IOException {
        if (profileImgUrl == null || profileImgUrl.isBlank()) {
            return ConstantUtil.PATH_DEFAULT_PROFILE;
        }

        return base64ToUrl(profileImgUrl);
    }

    @Transactional
    public UsersSignUpResponse signup(UsersSignUpRequest usersSignUpRequest) throws IOException {
        String email = usersSignUpRequest.getUserEmail();
        String password = usersSignUpRequest.getUserPassword();
        String nickname = usersSignUpRequest.getUserNickname();
        String profileImgUrl = checkAndSetProfileImage(usersSignUpRequest.getUserProfileImgUrl());

        nickname = nickname.trim();
        password = password.trim();
        email = email.trim();

        System.out.println(email);

        if (isEmailExist(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }

        if(isNicknameExist(nickname)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.");
        }

        // 데이터 저장
        Users user = Users.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .build();

        UserDetail userDetail = UserDetail.of(user);

        usersRepository.save(user);
        usersDetailRepository.save(userDetail);

        return UsersSignUpResponse.create(email, nickname, userDetail.getCreatedAt());
    }

    private String[] getIssueAndExpirationTimes(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return new String[]{dtf.format(now), dtf.format(now.plusDays(7))};
    }

    private Map<String,String> getAuthInfoMap(){
        Map<String,String> authInfoMap = new HashMap<>();
        String[] timestamp = getIssueAndExpirationTimes();
        authInfoMap.put("issuedAt",timestamp[0]);
        authInfoMap.put("expiresIn",timestamp[1]);
        return authInfoMap;
    }

    public void verifyPassword(Users users, String givenPassword){
        if(!BCrypt.checkpw(givenPassword, users.getPasswordHash())){
            throw new UsersException.WrongPasswordException("잘못된 비밀번호입니다.");
        }
    }

    @Transactional
    public UsersSignInResponse signIn(UsersSignInRequest UsersSignInRequest) {

        String email = UsersSignInRequest.getUserEmail();
        String password = UsersSignInRequest.getUserPassword();

        Users users = usersRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsersException.UsersNotFoundException("존재하지 않는 사용자입니다."));
        verifyPassword(users, password);

        Map<String,String> authInfoMap = getAuthInfoMap();

        return new UsersSignInResponse(
                users.getEmail(),
                authInfoMap.get("issuedAt"),
                authInfoMap.get("expiresIn")
        );
    }

    @Transactional
    public UsersSimpleResponse editPassword(Long id, UsersEditPasswordRequest usersEditPasswordRequest) {
        Users users = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UsersException.UsersNotFoundException("존재하지 않는 사용자입니다."));

        String oldPassword = usersEditPasswordRequest.getUserOldPassword();
        String newPassword = usersEditPasswordRequest.getUserNewPassword();

        verifyPassword(users, oldPassword);

        if (newPassword.equals(oldPassword)) {
            throw new UsersException.SamePasswordException("현재 비밀번호와 새 비밀번호가 동일합니다.");
        }

        String newPasswordHash = PasswordUtil.getHashedPassword(newPassword);
        users.updatePassword(newPasswordHash);

        return new UsersSimpleResponse(
                users.getId(),
                users.getNickname()
        );
    }

    @Transactional
    public UsersSimpleResponse editProfile(Long id, UsersEditProfileRequest usersEditProfileRequest) {
        Users users = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UsersException.UsersNotFoundException("존재하지 않는 사용자입니다."));

        String newNickname = usersEditProfileRequest.getUserNickname();
        String newProfileImgUrl = usersEditProfileRequest.getUseeProfileImgUrl();

        if (newNickname.equals(users.getNickname())) {
            throw new UsersException.SameNicknameException("이전 닉네임과 동일합니다.");
        };

        if (usersRepository.findByNickname(newNickname).isPresent()) {
            throw new UsersException.UsersNicknameAlreadyExistsException("존재하는 닉네임입니다.");
        }

        if (newProfileImgUrl == null){
            newProfileImgUrl = users.getProfileImgUrl();
        }

        users.updateProfile(newNickname, newProfileImgUrl);

        return new UsersSimpleResponse(
                users.getId(),
                users.getNickname()
        );
    }

    @Transactional
    public UsersSimpleResponse softDelete(Long id) {
        Users users = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UsersException.UsersNotFoundException("존재하지 않는 사용자입니다."));
        users.softDelete();

        return new UsersSimpleResponse(
                users.getId(),
                users.getNickname()
        );
    }

}
