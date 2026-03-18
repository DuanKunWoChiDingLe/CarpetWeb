package com.daocheng.api.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    private static final Map<Class<?>, Converter<String, ?>> converterMap = new HashMap<>();

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, ?> converter = converterMap.get(targetType);
        if (converter == null) {
            converter = new StringToEnumConverter<>(targetType);
            converterMap.put(targetType, converter);
        }
        return (Converter<String, T>) converter;
    }

    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            // 优先尝试调用枚举的 fromValue 静态方法
            try {
                Method method = enumType.getMethod("fromValue", String.class);
                return (T) method.invoke(null, source);
            } catch (Exception e) {
                // 回退到默认的 Enum.valueOf（忽略大小写）
                for (T constant : enumType.getEnumConstants()) {
                    if (constant.name().equalsIgnoreCase(source)) {
                        return constant;
                    }
                }
                throw new IllegalArgumentException("无法将字符串 [" + source + "] 转换为枚举类型 " + enumType.getSimpleName());
            }
        }
    }
}