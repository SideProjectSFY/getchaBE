package com.ssafy.backend.wish.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Wish {

    private Long id;
    private Long goodsId;
    private Long userId;
    private LocalDateTime createdAt;
}
