package com.generic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSection {

    private String id;
    private String title;
    private String description;
    private int order;
    private List<String> elementIds;
    private boolean visible;
    private boolean collapsed;
}