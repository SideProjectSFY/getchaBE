package com.ssafy.backend.comment.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    /**
     * 댓글 또는 대댓글 등록
     * @param comment 댓글 또는 대댓글 내용
     * @return 댓글 또는 대댓글 등록 결과 반환
     */
    int insertComment(Comment comment);

    /**
     * 해당 굿즈 글의 댓글과 대댓글 전체 조회
     * @param goodsId 굿즈ID 정보
     * @return 댓글과 대댓글 전체 조회 리스트
     */
    List<CommentResponseDTO> selectAllCommentByGoodsId(Long goodsId);

    /**
     * 댓글 또는 대댓글 수정
     * @param comment 수정할 댓글 또는 대댓글 내용
     * @return 댓글 또는 대댓글 수정 결과 반환
     */
    int updateComment(Comment comment);

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
     * soft delete 처리
     * - 대댓글이 있는 댓글인 경우 (삭제된 댓글입니다 처리를 위해)
     * @param commentId soft delete 할 댓글ID(pk)
     * @return soft Delete 댓글 결과 반환
     */
    int softDeleteComment(Long commentId, Long writerId);

    /**
     * hard delete 처리
     * - 대댓글이 없는(=삭제된) 댓글인 경우
     * - 대댓글인 경우
     * @param commentId hard delete 할 댓글 or 대댓글 ID(pk)
     * @return hard delete 댓글 or 대댓글 결과 반환
     */
    int hardDeleteComment(Long commentId, Long writerId);
}
