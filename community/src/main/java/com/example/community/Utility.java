package com.example.community;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utility {
    public static String getCreatedAt(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String getHashedPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
