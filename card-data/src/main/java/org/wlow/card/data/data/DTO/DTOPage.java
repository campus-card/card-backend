package org.wlow.card.data.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOPage<T>{
    Long total;
    Long pages;
    List<T> data;
}
