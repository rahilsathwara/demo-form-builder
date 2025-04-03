package com.generic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WidgetProperties {

    private boolean supportsValidation;
    private List<String> supportedValidations;
    private boolean hasOptions;
    private boolean hasPlaceholder;
    private boolean supportsDefaultValue;
    private boolean isContainer;
    private List<String> allowedChildTypes;
}