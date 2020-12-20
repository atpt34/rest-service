package com.oleksa.snapshot.entity;

import lombok.Data;

@Data
public class RandomParams {

    private final String apiKey = System.getProperty("apiKey");
    private int n = 1;
}
