package com.example.community.comments.dto;

import com.example.community.comments.entity.Comments;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "게시글 보기 응답 dto")
public class CommentsViewResponse {
    @Schema(description = "게시글 엔티티 리스트")
    private List<Comments> commentsEntities;
    public CommentsViewResponse() {}
    public CommentsViewResponse(List<Comments> commentsEntities) {
        this.commentsEntities = commentsEntities;
    }
}
