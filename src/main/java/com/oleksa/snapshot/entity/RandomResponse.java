package com.oleksa.snapshot.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Value;


@Data
public class RandomResponse {
    private int id;
    private String jsonrpc;
    private Result result;
}
