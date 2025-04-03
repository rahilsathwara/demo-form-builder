package com.generic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetTemplate {

    private String angular;
    private String react;
    private String vue;
    private String renderFunction;
    private String editFunction;
    private String validationFunction;
}
