package com.oleksa.snapshot.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
public class Result {
    private int bitsUsed;
    private int bitsLeft;
    private int requestsLeft;
    private int advisoryDelay;
    private RandomInner random;
}
