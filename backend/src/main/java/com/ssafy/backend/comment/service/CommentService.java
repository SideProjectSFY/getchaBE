package com.ssafy.backend.comment.service;

import com.ssafy.backend.comment.model.CommentRequestDTO;
import com.ssafy.backend.comment.model.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    void addComment(Long loginUserId, CommentRequestDTO.CommentRegister commentRegister);

    List<CommentResponseDTO> getAllComment(Long loginUserId, Long goodsId);

    void updateComment(Long loginUserId, CommentRequestDTO.CommentModify commentModify);

    void deleteComment(Long loginUserId, Long commentId);
}
