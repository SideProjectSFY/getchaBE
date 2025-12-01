package com.ssafy.backend.comment.service.impl;

import com.ssafy.backend.comment.model.Comment;
import com.ssafy.backend.comment.model.CommentMapper;
import com.ssafy.backend.comment.model.CommentRequestDTO;
import com.ssafy.backend.comment.model.CommentResponseDTO;
import com.ssafy.backend.comment.service.CommentService;
import com.ssafy.backend.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    /**
     * TODO
     * 1. 필수 !!!
     * - 토큰 정보 파싱 후 사용자 정보 받아오기
     */

    private final CommentMapper commentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(CommentRequestDTO.CommentRegister commentRegister) {
        // TODO : 로그인 되었는지 체크 !
        Long writerId = 1L; // TODO : 로그인 기능 구현 전이어서 현재는 고정값


        // parentId 가 null 이면 댓글, 대댓글이면 값이 존재한다.
        Comment comment = Comment.builder()
                .goodsId(commentRegister.getGoodsId())
                .writerId(writerId)
                .parentId(commentRegister.getParentId())
                .content(commentRegister.getContent())
                .build();

        // 댓글 등록
        int saveResult = commentMapper.insertComment(comment);
        // 댓글 등록에 실패 시 에러 던지기
        if(saveResult < 1) throw new CustomException("댓글 or 대댓글 등록에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public List<CommentResponseDTO> getAllComment(Long goodsId) {

        List<CommentResponseDTO> commentList = commentMapper.selectAllCommentByGoodsId(goodsId);
        if(commentList.isEmpty()) throw new NoSuchElementException("등록된 댓글이 없습니다.");

        Map<Long, CommentResponseDTO> map = new HashMap<>();
        List<CommentResponseDTO> rootList = new ArrayList<>();

        // 1) 맵에 부모댓글만 저장
        for (CommentResponseDTO c : commentList) {
            if(c.getParentId() == null) {
                map.put(c.getCommentId(), c);
                // replyList 초기화
                c.setReplyList(new ArrayList<>());
            }
        }

        // 2) parentId 기준으로 트리 구성
        for (CommentResponseDTO c : commentList) {
            if (c.getParentId() == null) {
                // 댓글(root)
                rootList.add(c);
            } else {
                // 대댓글 → 부모의 replyList에 추가
                CommentResponseDTO parent = map.get(c.getParentId());
                if (parent != null) {
                    parent.getReplyList().add(c);
                }
            }
        }

        return rootList;
    }

    @Override
    @Transactional
    public void updateComment(CommentRequestDTO.CommentModify commentModify) {
        // TODO : 본인이 쓴 댓글인지 확인 필요 (토큰에서 가져오기)
        Long writerId = 1L;
        
        Comment comment = Comment.builder()
                .id(commentModify.getCommentId())
                .writerId(writerId)
                .content(commentModify.getContent())
                .build();

        int updateResult = commentMapper.updateComment(comment);
        if(updateResult < 1) throw new CustomException("댓글 or 대댓글 수정에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        // TODO : 본인이 쓴 댓글인지 확인 필요 (토큰에서 가져오기)
        Long writerId = 1L;

        // 댓글이든 대댓글이든, 자식 댓글이 있는지 확인할 수 있음 ! 
        int replyCount = commentMapper.countChildCommentByParentId(commentId);
        
        // 대댓글이 존재하는 댓글일 경우, 
        if(replyCount > 0) {
            // soft delete 해서 '삭제된 댓글입니다' 처리
            int softResult = commentMapper.softDeleteComment(commentId, writerId);
            if(softResult < 1) throw new CustomException("댓글 삭제에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

            return;
        } else { // 대댓글이 없는 댓글 or 대댓글일 경우 
            // hard delete 해서 완전 삭제 처리 
            int hardDelete = commentMapper.hardDeleteComment(commentId, writerId);
            if(hardDelete < 1) throw new CustomException("댓글 or 대댓글 삭제에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

            // 대댓글의 부모 ID 와 soft delete 여부 조회
            Map<String, Object> parentCommentMap = commentMapper.selectParentIdAndDeletedAtByCommentId(commentId);
            Long parentId = (Long)parentCommentMap.get("parentId");

            // soft delete 가 된 부모댓글 일 경우
            if(!Objects.isNull(parentCommentMap.get("deleted_at")) && parentId != null) {
                // 대댓글 남아있는지 확인
                int reReplyCount = commentMapper.countChildCommentByParentId(parentId);
                // 대댓글이 없다면, 부모 댓글 hard delete 처리
                if(reReplyCount < 1) commentMapper.hardDeleteComment(commentId, writerId);
            }
        }
    }
}
