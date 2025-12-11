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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentResponseDTO.AddCommentResult addComment(Long loginUserId, CommentRequestDTO.CommentRegister commentRegister) {

        // 굿즈 글 존재 유무 확인
        int checkGoodsId = commentMapper.checkGoodsId(commentRegister.getGoodsId());
        if(checkGoodsId == 0) throw new NoSuchElementException("존재하지 않는 굿즈 글입니다.");

        // 부모 댓글 존재 유무 확인
        Long parentId = commentRegister.getParentId();
        if(parentId != null) {
            int checkParentId = commentMapper.checkParentId(parentId);
            if(checkParentId == 0) throw new NoSuchElementException("부모 댓글이 존재하지 않습니다.");
        }

        // parentId 가 null 이면 댓글, 대댓글이면 값이 존재한다.
        Comment comment = Comment.builder()
                .goodsId(commentRegister.getGoodsId())
                .writerId(loginUserId)
                .parentId(commentRegister.getParentId())
                .content(commentRegister.getContent())
                .build();

        // 댓글 등록
        int saveResult = commentMapper.insertComment(comment);
        // 댓글 등록에 실패 시 에러 던지기
        if(saveResult < 1) throw new CustomException("댓글 or 대댓글 등록에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

        return CommentResponseDTO.AddCommentResult.builder()
                .commentId(comment.getId())
                .parentId(parentId)
                .build();
    }

    @Override
    public List<CommentResponseDTO.CommentAll> getAllComment(Long loginUserId ,Long goodsId) {

        List<CommentResponseDTO.CommentAll> commentList = commentMapper.selectAllCommentByGoodsId(loginUserId, goodsId);
        if(commentList == null || commentList.isEmpty()) return Collections.emptyList();

        Map<Long, CommentResponseDTO.CommentAll> map = new HashMap<>();
        List<CommentResponseDTO.CommentAll> rootList = new ArrayList<>();

        // 1) 맵에 부모댓글만 저장
        for (CommentResponseDTO.CommentAll c : commentList) {
            if(c.getParentId() == null) {
                map.put(c.getCommentId(), c);
                // replyList 초기화
                c.setReplyList(new ArrayList<>());
            }
        }

        // 2) parentId 기준으로 트리 구성
        for (CommentResponseDTO.CommentAll c : commentList) {
            if (c.getParentId() == null) {
                // 댓글(root)
                rootList.add(c);
            } else {
                // 대댓글 → 부모의 replyList에 추가
                CommentResponseDTO.CommentAll parent = map.get(c.getParentId());
                if (parent != null) {
                    parent.getReplyList().add(c);
                }
            }
        }

        return rootList;
    }

    @Override
    @Transactional
    public void updateComment(Long loginUserId ,CommentRequestDTO.CommentModify commentModify) {

        Long writerId = commentMapper.selectWriterIdByCommentId(commentModify.getCommentId());

        if(writerId == null) throw new NoSuchElementException("존재하지 않는 댓글입니다.");

        // 작성권한체크
        if(!Objects.equals(writerId, loginUserId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        int updateResult = commentMapper.updateComment(loginUserId, commentModify);
        if(updateResult < 1) throw new CustomException("댓글 or 대댓글 수정에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    @Transactional
    public CommentResponseDTO.DeleteCommentResult deleteComment(Long loginUserId , Long commentId) {

        Long writerId = commentMapper.selectWriterIdByCommentId(commentId);

        if(writerId == null) throw new NoSuchElementException("존재하지 않는 댓글입니다.");

        // 작성권한체크
        if(!Objects.equals(writerId, loginUserId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // 반환값 초기화
        Long softDeletedId = null;
        List<Long> hardDeletedIds = new ArrayList<>();

        Long parentId = null;
        LocalDateTime deletedAt = null;

        // 1. commentId에 대한 parent 정보와 soft delete 여부를 한 번에 조회
        // commentId 가 부모 ID 일 경우 ->  둘다 null null 이 나올 것임
        // 이건 parentId 가 null 이 아닐때만 확인해야하는것.
        Map<String, Object> parentCommentMap =
                commentMapper.selectParentIdAndDeletedAtByCommentId(commentId);

        // 2. 부모 댓글이 삭제된 경우, 부모ID, 부모의 삭제일시 넣어주기
        if(!Objects.isNull(parentCommentMap) && parentCommentMap.get("deleted_at") != null) {
            parentId = (Long)parentCommentMap.get("parent_id");
            deletedAt = ((Timestamp) parentCommentMap.get("deleted_at")).toLocalDateTime(); // 부모댓글의 삭제일시
        }

        // 3. 댓글이든 대댓글이든, 자식 댓글이 있는지 확인 !
        int replyCount = commentMapper.countChildCommentByParentId(commentId);
        
        // 4-1. 대댓글이 존재하는 댓글일 경우 -> soft delete,
        if(replyCount > 0) {
            // soft delete 해서 '삭제된 댓글입니다' 처리
            int softResult = commentMapper.softDeleteComment(commentId, loginUserId);
            if(softResult < 1)
                throw new CustomException("댓글 삭제에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

            // soft delete 된 댓글Id 반환
            softDeletedId = commentId;
            return CommentResponseDTO.DeleteCommentResult.builder()
                    .softDeletedId(softDeletedId)
                    .hardDeletedIds(hardDeletedIds)
                    .build();

        }
        // 4-2. 대댓글이 없는 댓글 or 대댓글일 경우

        // hard delete 해서 완전 삭제 처리
        int hardDelete = commentMapper.hardDeleteComment(commentId, loginUserId);
        if(hardDelete < 1) throw new CustomException("댓글 or 대댓글 삭제에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

        // hard delete 된 댓글Id 추가
        hardDeletedIds.add(commentId);

        // 5. soft delete 가 된 부모댓글 일 경우 (삭제 요청 들어온게 대댓글 일 때만 진행됨)
        if(deletedAt != null && parentId != null) {
            // 대댓글 남아있는지 확인
            int reReplyCount = commentMapper.countChildCommentByParentId(parentId);
            // 대댓글이 없다면, 부모 댓글 hard delete 처리
            if(reReplyCount < 1) {
                int deleteParent = commentMapper.hardDeleteComment(parentId, loginUserId);
                if(deleteParent < 1)
                    throw new CustomException("댓글 or 대댓글 삭제에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);
                hardDeletedIds.add(parentId);
            }
        }

        return CommentResponseDTO.DeleteCommentResult.builder()
                .softDeletedId(softDeletedId)
                .hardDeletedIds(hardDeletedIds)
                .build();
    }
}
