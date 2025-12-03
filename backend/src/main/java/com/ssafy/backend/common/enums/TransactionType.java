package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    CHARGE,
    BIDLOCK,
    BIDUNLOCK,
    INCOME,
    EXPENSE;
}
