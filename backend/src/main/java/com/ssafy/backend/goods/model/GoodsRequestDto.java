package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.PageRequest;
import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
public class GoodsRequestDto {

    @Schema(description = "굿즈 등록 요청 DTO")
    @Getter
    @Setter
    public static class GoodsRegister {

        @Schema(description = "애니ID(pk)")
        @NotNull
        private Long animeId;
        @Schema(description = "카테고리", example = "figure")
        @NotBlank
        private Category category;

        @NotBlank
        private String title;
        @NotBlank
        private String description;
        @Schema(description = "시작가")
        @NotNull
        @Min(value = 1000, message = "최소 1000원 이상이어야합니다.")
        private int startPrice;
        @Min(value = 1000, message = "최소 1000원 이상이어야합니다.")
        private int instantBuyPrice;

        @Schema(description = "경매기간(일)", example = "3", defaultValue = "3")
        @Max(value = 14, message = "경매기간은 최대 14일까지 입니다.")
        private int duration;
    }

    @Schema(description = "굿즈 목록 조회 DTO")
    @Getter
    @Setter
    @ToString
    public static class GoodsLookUp extends PageRequest {
        private AuctionStatus auctionStatus;
        private Category category;
    }

    @Schema(description = "굿즈 수정 DTO")
    @Getter
    @Setter
    public static class GoodsModify {
        @NotBlank(message = "굿즈ID 는 필수값입니다.")
        private Long goodsId;

        @NotBlank
        private String title;
        @NotBlank
        private Long animeId;
        @NotBlank
        private String category;
        @NotBlank
        private String description;
        @NotBlank
        @Min(value = 1000, message = "최소 1000원 이상이어야합니다.")
        private int startPrice;
        @Min(value = 1000, message = "최소 1000원 이상이어야합니다.")
        private int instantBuyPrice;
        @NotBlank
        @Max(value = 14, message = "경매기간은 최대 14일까지 입니다.")
        private int duration;
        private String auctionStatus;
    }

}
