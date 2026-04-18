package com.saucedemo.models;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CartItem {
    private int quantity;
    private String name;
    private String description;
    private double price;
}
