package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public enum Category {

    FIGURE,
    PHOTOCARD,
    ACRYLICSTAND,
    KEYRING,
    DOLL,
    POSTER,
    BADGE,
    OTHER;


    /**
     * 프론트에서 던져준 파라미터를 Enum 타입으로 바꿔 반환하는 메서드
     * @param category  문자열타입
     * @return Category Enum타입
     */
    /*public static Category getCategory(Category category) {

        if(category == null) return OTHER;

        for (Category c : Category.values()) {
            if (c.category.equals(category)) {
                return c;
            }
            else throw new NoSuchElementException("해당 카테고리는 존재하지 않습니다.");
        }

        return null;
    }*/
}
