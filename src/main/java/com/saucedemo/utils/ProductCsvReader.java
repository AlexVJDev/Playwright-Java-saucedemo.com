package com.saucedemo.utils;

import com.saucedemo.models.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductCsvReader {
    public static List<Product> readProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                products.add(new Product(values[0], values[1],
                        Double.parseDouble(values[2]), ProductImageNameNormalizer.normalize(values[3])));
            }
        } catch (IOException e) {
            throw new RuntimeException("Wasn't able to read file:" + filePath, e);
        }
        return products;
    }
}
