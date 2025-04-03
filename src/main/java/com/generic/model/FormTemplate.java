package com.generic.model;

import com.generic.enumration.TemplateCategory;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "form_templates")
public class FormTemplate {
    @Id
    private String id;

    private String name;
    private String description;
    private TemplateCategory category;
    private String thumbnail;
    private Forms formDefinition;
    private int usageCount;
    private boolean isSystem;

    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
