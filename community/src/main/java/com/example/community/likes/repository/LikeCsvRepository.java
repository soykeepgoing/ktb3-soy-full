package com.example.community.likes.repository;

import com.example.community.likes.entity.Likes;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class LikeCsvRepository implements LikeRepository {
    Map<String, Likes> likeStore = new HashMap<>();
    String dbPath;
    String contentType;

    public abstract void init();

    public String key(Long contentId, Long userId){
        return contentId + "-" + userId;
    }

    public Likes createEntityFromLine(String line){
        line.split(",");
        Long contentId = Long.valueOf(line.split(",")[1]);
        Long userId = Long.valueOf(line.split(",")[2]);
        String createdAt = line.split(",")[3];
        return Likes.of(contentId, userId, createdAt);
    }

    public void initFromFile() {
        File file = new File(dbPath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null){
                Likes likes = createEntityFromLine(line);
                save(likes);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Likes save(Likes entity) {
        String key = key(entity.getContentId(), entity.getUserId());
        likeStore.put(key, entity);
        return entity;
    }

    public void deleteByContentAndUserId(Long contentId, Long userID){
        String key = key(contentId, userID);
        likeStore.remove(key);
    }

    public boolean existsByContentAndUserId(Long contentId, Long userID) {
        String key = key(contentId, userID);
        return likeStore.containsKey(key);
    }
}
