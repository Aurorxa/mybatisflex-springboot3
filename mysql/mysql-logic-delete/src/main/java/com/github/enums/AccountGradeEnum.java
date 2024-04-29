package com.github.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountGradeEnum {
    EXCELLENT(0, "优秀"),
    PASS(1, "及格"),
    POOR(2, "差");

    @EnumValue
    private final int code;

    private final String desc;
}
