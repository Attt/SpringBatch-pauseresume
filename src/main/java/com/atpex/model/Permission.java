package com.atpex.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Permission {
    private Long id;

    private String url;

    private String name;
}