package com.ssafy.backend.comment.model;

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
    private String writerNickName;
    private String writerProfileFilePath;
    private String content;
    private LocalDateTime createdAt;

    @Setter
    private List<CommentResponseDTO> replyList;

}
