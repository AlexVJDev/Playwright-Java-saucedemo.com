package com.saucedemo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class Product {
    private String name;
    private String description;
    private double price;
    private String imageName;
}
