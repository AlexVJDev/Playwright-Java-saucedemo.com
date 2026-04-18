package com.saucedemo.utils;

import com.saucedemo.models.SellSummeryInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SellSummeryInfoCsvReader {

    public static SellSummeryInfo readProducts(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line = br.readLine();
            if (line == null || line.isBlank()) {
                throw new IllegalStateException("No data rows in CSV: " + filePath);
            }
            String[] values = line.split(";", -1);
            if (values.length < 5) {
                throw new IllegalStateException(
                        "Expected 5 columns, got " + values.length + " in line: " + line);
            }
            return new SellSummeryInfo(
                    values[0].trim(),
                    values[1].trim(),
                    Double.parseDouble(values[2].trim()),
                    Double.parseDouble(values[3].trim()),
                    Double.parseDouble(values[4].trim())
            );
        } catch (IOException e) {
            throw new RuntimeException("Wasn't able to read file:" + filePath, e);
        }
    }
}
