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
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GoodsRequestDto {

    @Schema(description = "굿즈 등록 요청 DTO")
    @Getter
    @Setter
    public static class GoodsRegister {

        @Schema(description = "애니ID(pk)")
        @NotNull
        private Long animeId;
        @Schema(description = "카테고리", example = "FIGURE")
        @NotNull
        private Category category;

        @NotBlank
        private String title;
        @NotBlank
        private String description;
        @Schema(description = "시작가")
        @NotNull
        @Min(value = 1000, message = "최소 1000원 이상이어야합니다.")
        @Range(min = 1000, max = 5000000, message = "금액은 1000원 이상 500만원 이하이어야 합니다.")
        private Integer startPrice;
        @Range(min = 1000, max = 5000000, message = "금액은 1000원 이상 500만원 이하이어야 합니다.")
        private Integer instantBuyPrice;

        @Schema(description = "경매기간(일)", example = "3", defaultValue = "3")
        @Max(value = 14, message = "경매기간은 최대 14일까지 입니다.")
        private Integer duration;
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
        @Schema(name = "deleteImageIds", description = "삭제할 이미지파일ID 리스트")
        private List<Long> deleteImageIds;
        @Schema(name = "existingImages", description = "기존 이미지 정보 리스트")
        private List<GoodsExistingImages> existingImages;

        @NotNull(message = "판매자ID는 필수값입니다.")
        private Long sellerId;
        @NotNull(message = "굿즈ID 는 필수값입니다.")
        private Long goodsId;
        @NotBlank
        private String title;
        @NotNull
        private Long animeId;
        @NotBlank
        private String description;

        @NotNull
        @Range(min = 1000, max = 5000000, message = "금액은 1000원 이상 500만원 이하이어야 합니다.")
        private Integer startPrice;
        @Range(min = 1000, max = 5000000, message = "금액은 1000원 이상 500만원 이하이어야 합니다.")
        private Integer instantBuyPrice;

        @Schema(description = "경매상태", example = "WAIT")
        @NotNull(message = "경매상태가 유효하지 않습니다.")
        private AuctionStatus auctionStatus;
        @Schema(description = "카테고리", example = "FIGURE")
        @NotNull(message = "카테고리가 유효하지 않습니다.")
        private Category category;

        @NotNull
        @Max(value = 14, message = "경매기간은 최대 14일까지 입니다.")
        private Integer duration;
        @NotNull(message = "작성일시는 필수값입니다.")
        private LocalDateTime createdAt;

    }

    @Getter
    @Setter
    public static class GoodsExistingImages {
        private Long imageId;
        private int sortOrder;
    }

}
