package com.generic.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElementOption {

    private String optionId;
    private String label;
    private String value;
    private int order;
    private boolean isDefault;
}