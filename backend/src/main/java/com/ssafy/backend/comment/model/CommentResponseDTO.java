package com.ssafy.backend.comment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
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
    private List<CommentResponseDTO> replyList;

}
