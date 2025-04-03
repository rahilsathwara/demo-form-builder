package com.generic.model;

import com.generic.enumration.ValidationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Validation {
    private ValidationType type;
    private String value;
    private String message;
}
