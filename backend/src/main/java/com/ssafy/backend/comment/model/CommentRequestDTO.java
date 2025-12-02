package com.ssafy.backend.comment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommentRequestDTO {

    @Schema(description = "댓글 등록 요청 DTO")
    @Getter
    @Setter
    public static class CommentRegister {
        @NotNull
        private Long goodsId;
        private Long parentId;
        @NotBlank
        private String content;
    }

    @Schema(description = "댓글 수정 요청 DTO")
    @Getter
    @Setter
    public static class CommentModify {
        @Schema(hidden = true)
        private Long loginUserId;
        @NotBlank
        private String content;
        @NotNull
        private Long commentId;
    }
}
