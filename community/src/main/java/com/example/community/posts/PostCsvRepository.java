package com.example.community.posts;

import com.example.community.posts.entity.Posts;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostCsvRepository implements PostRepository {
    public final Map<Long, Posts> postStore = new LinkedHashMap<>();
    private AtomicLong sequence = new AtomicLong(0);

    private Posts createPostEntity(String line){
        String[] parts = line.split(",");
        Long postId = Long.parseLong(parts[0]);
        Long writerId = Long.parseLong(parts[1]);
        String title = parts[2];
        String content = parts[3];
        String imgUrl = parts[4];
        Long likeCounts = Long.parseLong(parts[5]);
        Long viewCounts = Long.parseLong(parts[6]);
        Long commentCounts = Long.parseLong(parts[7]);
        String createdAt = parts[8];

        return Posts.builder()
                .postId(postId)
                .postWriterId(writerId)
                .postTitle(title)
                .postContent(content)
                .postImgUrl(imgUrl)
                .postLikeCounts(likeCounts)
                .postViewCounts(viewCounts)
                .postCommentCounts(commentCounts)
                .postCreatedAt(createdAt)
                .build();
    }

    @PostConstruct
    private void init() throws IOException {
        File file = new File(PostsConstants.PATH_DB);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        bufferedReader.readLine(); // 칼럼행 건너뛰기
        while ((line = bufferedReader.readLine()) != null) {
            Posts posts = createPostEntity(line);
            sequence.set(posts.getPostId());
            postStore.put(sequence.get(), posts);
        }
    }

    public PostCsvRepository() throws IOException {
        init();
    }

    @Override
    public Optional<Posts> findById(Long postId) {
        return Optional.ofNullable(postStore.get(postId));
    }

    @Override
    public ArrayList<Posts> findAll() {
        return new ArrayList<>(postStore.values());
    }

    @Override
    public Posts save(Posts posts) {
        posts.updatePostId(sequence.incrementAndGet());
        postStore.put(posts.getPostId(), posts);
        return posts;
    }

    @Override
    public void delete(Long postId) {
        postStore.remove(postId);
    }

    @Override
    public boolean existsById(Long postId) {
        return postStore.containsKey(postId);
    }

    @Override
    public void edit(Posts posts){
        postStore.put(posts.getPostId(), posts);
    }

    @Override
    public void incrementLikeCount(Long postId){
        Posts posts = postStore.get(postId);
        posts.updateLikeCounts();
        postStore.put(postId, posts);
    }

    @Override
    public void decrementLikeCount(Long postId){
        Posts posts = postStore.get(postId);
        posts.decrementLikeCount();
        postStore.put(postId, posts);
    }

    @Override
    public List<Posts> findPagedPosts(Long startPageId, Long endPageId){
        List<Posts> sortedPostsList = postStore.values().stream().sorted(
                Comparator.comparing(Posts::getPostCreatedAt).reversed()
        ).toList();
        return sortedPostsList.subList(Math.toIntExact(startPageId), Math.toIntExact(endPageId));
    }
}
