package com.oleksa.snapshot.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RandomRequest {

    private int id;
    private final String jsonrpc = "2.0";
    private final String method = "generateUUIDs";
    private final RandomParams params = new RandomParams();
}
