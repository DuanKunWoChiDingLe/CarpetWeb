package com.daocheng.api.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LayTypeEnum {
    FULL("full", "满铺毯"),
    MODULAR("modular", "方块毯");

    @EnumValue
    private final String value;
    private final String desc;

    LayTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static LayTypeEnum fromValue(String input) {
        if (input == null) return null;
        // 尝试按英文 value 匹配
        for (LayTypeEnum type : LayTypeEnum.values()) {
            if (type.value.equalsIgnoreCase(input)) {
                return type;
            }
        }
        // 尝试按中文描述匹配（兼容前端可能直接传中文）
        for (LayTypeEnum type : LayTypeEnum.values()) {
            if (type.desc.equals(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的铺设方式: " + input);
    }
}