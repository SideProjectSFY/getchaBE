package com.ssafy.backend.comment.service;

import com.ssafy.backend.comment.model.CommentRequestDTO;
import com.ssafy.backend.comment.model.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    void addComment(CommentRequestDTO.CommentRegister commentRegister);

    List<CommentResponseDTO> getAllComment(Long goodsId);

    void updateComment(CommentRequestDTO.CommentModify commentModify);

    void deleteComment(Long commentId);
}
