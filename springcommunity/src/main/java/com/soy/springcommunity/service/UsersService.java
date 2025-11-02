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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
        return usersRepository.findByEmail(email).isPresent();
    }

    private boolean isNicknameExist(String nickname) {
        return usersRepository.findByNickname(nickname).isPresent();
    }

    private String checkAndSetProfileImage(String profileImgUrl) {
        if (profileImgUrl == null || profileImgUrl.isBlank()) {
            return ConstantUtil.PATH_DEFAULT_PROFILE;
        }
        return profileImgUrl;
    }

    @Transactional
    public UsersSignUpResponse signup(UsersSignUpRequest usersSignUpRequest) {
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
        if(!users.isPasswordMatch(givenPassword)){
            throw new UsersException.WrongPasswordException("잘못된 비밀번호입니다.");
        }
    }

    private Users getUserEntityByEmail(String email){
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsersException.UsersNotFoundException("존재하지 않는 사용자입니다."));

        if (users.getIsDeleted()){
            throw new UsersException.UsersNotFoundException("탈퇴한 유저입니다.");
        }
        return users;
    }

    private Users findActiveUserById(Long id){
        Users users = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UsersException.UsersNotFoundException("존재하지 않는 사용자입니다."));
        return users;
    }

    @Transactional
    public UsersSignInResponse signIn(UsersSignInRequest UsersSignInRequest) {

        String email = UsersSignInRequest.getUserEmail();
        String password = UsersSignInRequest.getUserPassword();

        Users users = getUserEntityByEmail(email);
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
        Users users = findActiveUserById(id);

        String oldPassword = PasswordUtil.getHashedPassword(
                usersEditPasswordRequest.getUserOldPassword());
        String newPassword = PasswordUtil.getHashedPassword(
                usersEditPasswordRequest.getUserNewPassword());

        if (users.isPasswordMatch(oldPassword)) {
            throw new UsersException.InvalidCurrentPasswordException("잘못된 비밀번호입니다.");
        }

        if (newPassword.equals(oldPassword)) {
            throw new UsersException.SamePasswordException("현재 비밀번호와 새 비밀번호가 동일합니다.");
        }

        users.updatePassword(newPassword);

        return new UsersSimpleResponse(
                users.getId(),
                users.getNickname()
        );
    }

    @Transactional
    public UsersSimpleResponse editProfile(Long id, UsersEditProfileRequest usersEditProfileRequest) {
        Users users = findActiveUserById(id);

        String newNickname = usersEditProfileRequest.getNickname();
        String newProfileImgUrl = usersEditProfileRequest.getProfileImgUrl();

        if (newNickname.equals(users.getNickname())) {
            throw new UsersException.SameNicknameException("이전 닉네임과 동일합니다.");
        };

        if (usersRepository.findByNickname(newNickname).isPresent()) {
            throw new UsersException.UsersNicknameAlreadyExistsException("존재하는 닉네임입니다.");
        }

        if (newProfileImgUrl != "" & newProfileImgUrl.equals(users.getProfileImgUrl())) {
            throw new UsersException.SameProfileImgException("이전 프로필 사진과 동일합니다.");
        }

        users.updateProfile(newNickname, newProfileImgUrl);

        return new UsersSimpleResponse(
                users.getId(),
                users.getNickname()
        );
    }

    @Transactional
    public UsersSimpleResponse softDelete(Long id) {
        Users users = findActiveUserById(id);
        users.softDelete();

        return new UsersSimpleResponse(
                users.getId(),
                users.getNickname()
        );
    }

}
