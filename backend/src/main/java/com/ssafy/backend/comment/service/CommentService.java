package com.ssafy.backend.comment.service;

import com.ssafy.backend.comment.model.CommentRequestDTO;
import com.ssafy.backend.comment.model.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    CommentResponseDTO.AddCommentResult addComment(Long loginUserId, CommentRequestDTO.CommentRegister commentRegister);

    List<CommentResponseDTO.CommentAll> getAllComment(Long loginUserId, Long goodsId);

    void updateComment(Long loginUserId, CommentRequestDTO.CommentModify commentModify);

    CommentResponseDTO.DeleteCommentResult deleteComment(Long loginUserId, Long commentId);
}
