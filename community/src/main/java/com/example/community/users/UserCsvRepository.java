package com.example.community.users;
import com.example.community.Utility;
import com.example.community.users.entity.Users;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class UserCsvRepository implements UserRepository {
    public final Map<Long, Users> userStore = new LinkedHashMap<>();
    private AtomicLong sequence = new AtomicLong(0);

    private Users createUserDto(String line){
        String[] parts = line.split(",", -1);
        Long userId = Long.valueOf(parts[0]);
        String userEmail = parts[1];
        String userPasswordHash = Utility.getHashedPassword(parts[2]);
        String userNickname = parts[3];
        String userProfilePic = parts[4];
        Boolean userIsDeleted = Boolean.valueOf(parts[5]);
        String userCreatedAt = parts[6];
        String userDeletedAt = null;

        Users users = Users.builder()
                .email(userEmail)
                .password(userPasswordHash)
                .nickname(userNickname)
                .profileImgUrl(userProfilePic)
                .build();
        return users;
    }

    private void init() throws IOException {
        File file = new File(UsersConstants.PATH_DB);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        bufferedReader.readLine(); // 칼럼행 건너뛰기
        while ((line = bufferedReader.readLine()) != null) {
            Users users = createUserDto(line);
            sequence.set(users.getId());
            userStore.put(sequence.get(), users);
        }
    }

    public UserCsvRepository() throws IOException {
        init();
    }

    @Override
    public ArrayList<Users> findAll() {return new ArrayList<>(userStore.values());}

    @Override
    public Optional<Users> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public Optional<Users> findByNickname(String nickname) {
        return userStore.values().stream()
                .filter(item -> item.getNickname().equals(nickname))
                .findAny();
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return userStore.values().stream()
                .filter(item -> item.getEmail().equals(email))
                .findAny();
    }

    @Override
    public ArrayList<Users> findAllByIds(List<Long> ids) {
        return userStore.entrySet().stream()
                .filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public Optional<Users> findNotDeletedById(Long id) {
        Users users = userStore.get(id);
        if (users != null && !users.getIsDeleted()){
            return Optional.of(users);
        }
        return Optional.empty();
    }

    @Override
    public void editPassword(Users users, String newPassword) {
        String hashedNewPassword = Utility.getHashedPassword(newPassword);
        users.updatePassword(hashedNewPassword);
        userStore.put(users.getId(), users);
    }

    @Override
    public void editProfile(Users users, String newNickname, String newProfileImgUrl) {
        if (!(newProfileImgUrl == null || newProfileImgUrl.isEmpty())) {
            users.updateProfileImgUrl(newProfileImgUrl);
        }
        users.updateUserNickname(newNickname);
        userStore.put(users.getId(), users);
    }

    @Override
    public Users save(Users users) {
        long userNextId = sequence.incrementAndGet();
        users.updateUserId(userNextId);
        userStore.put(users.getId(), users);
        return users;
    }

    @Override
    public boolean existsById(Long id) {
        return userStore.containsKey(id);
    }

    @Override
    public void delete(Long id) {
        Users users = userStore.get(id);
        users.updateUserIsDeleted(true);
        userStore.put(users.getId(), users);
    }

    @Override
    public void softDelete(Users users) {
        users.updateUserIsDeleted(true);
        userStore.put(users.getId(), users);
    }
}
