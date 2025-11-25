package com.ssafy.backend.goods.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * GoodsImage 테이블의 컬럼과 거의 똑같이 사용하는 Model
 * */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsImage {

    private Long id;
    private Long goodsId;
    private String filePath;
    private String originFilename;
    private String storedFilename;
    private Long fileSize;
    private int sortOrder;
    private LocalDateTime createdAt;

}
