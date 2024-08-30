package org.wlow.card.application.config;

import com.baomidou.mybatisplus.annotation.IEnum;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举转换器工厂, 根据Controller中的参数类型, 以及接收到的字符串内容, 获取转换器实例并将字符串转换为枚举实例(IEnum)
 */
@NonNullApi
public class StringValueToEnumConverterFactory implements ConverterFactory<String, IEnum> {
    // 存储已经加载过的枚举转换器
    private static final Map<Class<? extends IEnum>, Converter> CONVERTERS = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEnum> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new StringValueToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }

    /**
     * 通配的枚举转换器: value的字符串 -> 枚举实例 <br>
     * 非JSON请求时, 无论前端发送数据的时候如何定义类型, controller中都是接受到字符串
     *     所以泛型定义为<String, T>
     *     String对应枚举的value值的字符串形式
     * @param <T> 实际的枚举类型, 需要有一个getValue方法, 而MyBatisPlus中的IEnum接口刚好符合要求
     */
    @NonNullApi
    private static class StringValueToEnumConverter<T extends IEnum> implements Converter<String, T> {
        private final Map<String, T> enumMap = new HashMap<>();
        private final Class<T> enumType;

        public StringValueToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                enumMap.put(e.getValue().toString(), e);
            }
        }

        @Override
        public T convert(String source) {
            T e = enumMap.get(source);
            if (e != null) {
                return e;
            }
            // 在GlobalExceptionHandler中处理
            throw new IllegalArgumentException("枚举 " + enumType.getName() + " 不具有的值: [" + source + "]");
        }
    }
}
