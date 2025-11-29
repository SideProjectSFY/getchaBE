package com.ssafy.backend.comment.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Comment 테이블의 컬럼과 거의 똑같이 사용하는 Model
 * */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment {

    private Long id;
    private Long goods_id;
    private Long writer_id;
    private Long parent_id;
    private String content;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
