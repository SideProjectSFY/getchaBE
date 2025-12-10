package com.ssafy.backend.comment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentAll {
        private Long commentId;
        private Long parentId;
        private Long goodsId;
        private Long writerId;
        @Schema(name = "checkWriter", description = "로그인한 사용자가 작성한 댓글인지 체크하는 컬럼")
        private boolean checkWriter;
        private String writerNickName;
        private String writerProfileFilePath;
        private String content;
        private LocalDateTime createdAt;

        @Setter
        private List<CommentResponseDTO.CommentAll> replyList;
    }

    @Schema(description = "댓글 또는 대댓글 등록 시 반환할 댓글Id(+부모댓글Id)")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCommentResult {
        private Long commentId;
        private Long parentId;
    }

    @Schema(description = "댓글 또는 대댓글 삭제 시 반환할 댓글Id")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteCommentResult {

        private Long softDeletedId;        // null 가능
        private List<Long> hardDeletedIds; // 0~2개

    }

}
