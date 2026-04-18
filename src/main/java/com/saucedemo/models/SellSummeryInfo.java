package com.saucedemo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class SellSummeryInfo {
    private String paymentInformation;
    private String shippingInformation;
    private double priceTotal;
    private double tax;
    private double total;
}
