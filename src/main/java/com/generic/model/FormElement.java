package com.generic.model;

import com.generic.enumration.ElementType;
import com.generic.enumration.ElementWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "form_elements")
public class FormElement {

    @Id
    private String id;
    private ElementType type;
    private String label;
    private String name;
    private int order;
    private boolean required;
    private boolean visible;
    private ElementWidth width;
    private String formId;
    private String parentId;
    private List<Validation> validations;
    private Map<String, Object> properties;
    private List<ElementOption> options;
}