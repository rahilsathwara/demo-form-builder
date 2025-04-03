package com.generic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theme {
    private String primaryColor;
    private String backgroundColor;
    private String fontFamily;
    private String borderRadius;
    private String buttonStyle;
    private String inputStyle;
}
