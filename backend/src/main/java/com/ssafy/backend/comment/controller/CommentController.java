package com.ssafy.backend.comment.controller;

import com.ssafy.backend.comment.model.CommentRequestDTO;
import com.ssafy.backend.comment.model.CommentResponseDTO;
import com.ssafy.backend.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Comment Rest API
 */

@RequestMapping("/comment")
@Tag(name = "Comment API", description = "댓글(+대댓글) 등록, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 or 대댓글 등록",
            description = "새로운 댓글 or 대댓글을 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 or 대댓글이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈 글입니다. or " +
                    "부모 댓글이 존재하지 않습니다."),
            @ApiResponse(responseCode = "503", description = "댓글 or 대댓글 등록에 실패하였습니다")
    })
    @PostMapping
    public ResponseEntity<String> postComment(@Valid @ModelAttribute CommentRequestDTO.CommentRegister commentRegister) {
        commentService.addComment(commentRegister);
        return new ResponseEntity<>("댓글 or 대댓글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
    }

    @Operation(
            summary = "댓글 or 대댓글 수정",
            description = "댓글 or 대댓글을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 or 대댓글이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "503", description = "댓글 or 대댓글 수정에 실패하였습니다")
    })
    @PutMapping
    public ResponseEntity<String> updateComment(@Valid @ModelAttribute CommentRequestDTO.CommentModify commentModify) {
        commentService.updateComment(commentModify);
        return new ResponseEntity<>("댓글 or 대댓글이 성공적으로 수정되었습니다.", HttpStatus.OK);
    }

    @Operation(
            summary = "댓글 or 대댓글 목록 조회",
            description = "댓글 or 대댓글 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "등록된 댓글이 없습니다."),
    })
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComment(@NotNull @RequestParam Long goodsId) {
        List<CommentResponseDTO> resultList = commentService.getAllComment(goodsId);
        return ResponseEntity.ok(resultList);
    }


    @Operation(
            summary = "댓글 or 대댓글 삭제",
            description = "댓글 or 대댓글을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 or 대댓글이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "503", description = "댓글 or 대댓글 삭제에 실패하였습니다")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteComment(@NotNull @RequestParam Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>("댓글 or 대댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
    }



}
