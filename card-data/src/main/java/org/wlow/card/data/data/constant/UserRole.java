package org.wlow.card.data.data.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole implements IEnum<Integer> {
    Student(1),
    Shop(2),
    Admin(3);

    @JsonValue
    public final Integer value;
}
