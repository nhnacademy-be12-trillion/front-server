package com.nhnacademy.frontserver.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateRequestDto {
    private Integer cartQuantity;
}