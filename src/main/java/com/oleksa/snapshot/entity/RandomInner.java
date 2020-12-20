package com.oleksa.snapshot.entity;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RandomInner {
    private String completionTime;
    private List<UUID> data = Lists.newArrayList();
}
