package com.generic.model;

import com.generic.enumration.ScriptTriggerEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormScript {

    private String id;
    private String name;
    private String description;
    private String triggerElement;
    private ScriptTriggerEvent triggerEvent;
    private String code;
    private boolean isActive;
    private int order;
}
