package com.example.community.comments;

import com.example.community.comments.entity.Comments;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CommentsCsvRepository implements CommentsRepository {
    public final Map<Long, Comments> commentsStore = new LinkedHashMap<>();
    private AtomicLong sequence = new AtomicLong(0);

    public Comments createCommentEntity(String line){
        String[] parts = line.split(",", -1);
        Long commentId = parseLong(parts[0]);
        Long parentCommentId = parseLong(parts[1]);
        Long postId = parseLong(parts[2]);
        Long writerId = parseLong(parts[3]);
        String commentContent = parts[4];
        String createdAt = parts[5];
        return Comments.builder()
                .commentId(commentId)
                .parentCommentId(parentCommentId)
                .postId(postId)
                .commentWriterId(writerId)
                .commentContent(commentContent)
                .commentCreatedAt(createdAt).build();
    }

    public Long parseLong(String value){
        if(value==null || value.isEmpty()){return null;}
        return Long.parseLong(value);
    }

    public CommentsCsvRepository()  throws IOException {
        init();
    }

    private void init() throws IOException {
        File file = new File(CommentsConstants.PATH_COMMENTS);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        bufferedReader.readLine(); // 칼럼행 건너뛰기
        while ((line = bufferedReader.readLine()) != null) {
            Comments commentEntity = createCommentEntity(line);
            Long id = commentEntity.getCommentId();
            sequence.set(id);
            commentsStore.put(id, commentEntity);
        }
    }

    @Override
    public Optional<Comments> findById(Long commentId) {
        return Optional.ofNullable(commentsStore.get(commentId));
    }

    @Override
    public ArrayList<Comments> findAll() {
        return new ArrayList<>(commentsStore.values());
    }

    @Override
    public Comments save(Comments comments) {
        Long newCommentId = sequence.incrementAndGet();
        comments.updateId(newCommentId);
        commentsStore.put(newCommentId, comments);
        return comments;
    }

    @Override
    public void delete(Long id) {
        commentsStore.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return commentsStore.containsKey(id);
    }

    @Override
    public ArrayList<Comments> getCommentsByPostId(Long postId) {
        List<Comments> commentsList = commentsStore.values().stream()
                .filter(commentsEntity -> commentsEntity.getPostId().equals(postId))
                .collect(Collectors.toList());
        return new ArrayList<>(commentsList);
    }

    @Override
    public void editComment(Long commentId, String newCommentContent) {
        Comments comments = commentsStore.get(commentId);
        comments.updateContent(newCommentContent);
        commentsStore.put(comments.getCommentId(), comments);
    }

}
