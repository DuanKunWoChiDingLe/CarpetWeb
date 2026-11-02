package com.daocheng.api.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MaterialEnum {
    POLYPROPYLENE("polypropylene", "丙纶"),
    POLYESTER("polyester", "涤纶"),
    NYLON("nylon", "尼龙");

    @EnumValue
    private final String value;
    private final String desc;

    MaterialEnum(String value, String desc) {
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
    public static MaterialEnum fromValue(String input) {
        if (input == null) return null;
        // 尝试按英文 value 匹配
        for (MaterialEnum type : MaterialEnum.values()) {
            if (type.value.equalsIgnoreCase(input)) {
                return type;
            }
        }
        // 尝试按中文描述匹配（兼容前端可能直接传中文）
        for (MaterialEnum type : MaterialEnum.values()) {
            if (type.desc.equals(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的铺设方式: " + input);
    }
}