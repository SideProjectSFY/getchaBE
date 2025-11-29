package com.ssafy.backend.comment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Comment Rest API
 */

@RequestMapping("/comment")
@Tag(name = "Comment API", description = "댓글(+대댓글) 등록, 조회, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
public class CommentController {
}
