package com.generic.model;

import com.generic.enumration.WidgetCategory;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "widgets")
public class Widget {
    @Id
    private String id;

    private String type;
    private String name;
    private String description;
    private String icon;
    private WidgetCategory category;
    private String createdBy;
    private boolean isSystem;
    private WidgetProperties properties;
    private Map<String, Object> defaultProperties;
    private WidgetTemplate template;
}
