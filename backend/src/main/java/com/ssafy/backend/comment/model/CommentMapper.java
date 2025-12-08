package com.ssafy.backend.comment.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    /**
     * 댓글을 작성하려는 굿즈 글의 존재 유무 확인
     * @param goodsId 굿즈ID(pk)
     * @return 굿즈 글 존재 유무 결과 반환
     */
    int checkGoodsId(Long goodsId);

    /**
     * 대댓글을 작성하려는 댓글의 존재 유무 확인
     * @param parentId 부모댓글ID(pk)
     * @return 댓글 존재 유무 결과 반환
     */
    int checkParentId(Long parentId);

    /**
     * 댓글 또는 대댓글 등록
     * @param comment 댓글 또는 대댓글 내용
     * @return 댓글 또는 대댓글 등록 결과 반환
     */
    int insertComment(Comment comment);

    /**
     * 해당 굿즈 글의 댓글과 대댓글 전체 조회
     * @param loginUserId 로그인한 사용자Id
     * @param goodsId 굿즈ID 정보
     * @return 댓글과 대댓글 전체 조회 리스트
     */
    List<CommentResponseDTO> selectAllCommentByGoodsId(Long loginUserId, Long goodsId);

    /**
     * 댓글 또는 대댓글 수정
     * @param loginUserId 로그인한 사용자Id
     * @param commentModify 수정할 댓글 또는 대댓글 내용
     * @return 댓글 또는 대댓글 수정 결과 반환
     */
    int updateComment(@Param("loginUserId") Long loginUserId
            , @Param("commentModify") CommentRequestDTO.CommentModify commentModify);

    /**
     * 자식 댓글의 개수 조회
     * @param parentId 부모댓글ID(pk)
     * @return 대댓글 개수 조회
     */
    int countChildCommentByParentId(Long parentId);

    /**
     * 대댓글의 부모ID와 soft delete 여부 조회
     * @param commentId 대댓글ID(pk)
     * @return 부모댓글 ID(pk)
     */
    Map<String, Object> selectParentIdAndDeletedAtByCommentId(Long commentId);

    /**
     * hard delete 할 댓글 or 대댓글 ID(pk)soft delete 처리
     * - 대댓글이 있는 댓글인 경우 (삭제된 댓글입니다 처리를 위해)
     * @param commentId soft delete 할 댓글ID(pk)
     * @param loginUserId 로그인한 사용자ID(pk)
     * @return soft Delete 댓글 결과 반환
     */
    int softDeleteComment(Long commentId, Long loginUserId);

    /**
     * hard delete 처리
     * - 대댓글이 없는(=삭제된) 댓글인 경우
     * - 대댓글인 경우
     * @param commentId hard delete 할 댓글 or 대댓글 ID(pk)
     * @param loginUserId 로그인한 사용자ID(pk)
     * @return hard delete 댓글 or 대댓글 결과 반환
     */
    int hardDeleteComment(Long commentId, Long loginUserId);
}
