package com.generic.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "form_versions")
public class FormVersion {

    @Id
    private String id;

    private String formId;
    private Integer versionNumber;
    private String formData;
    private String createdBy;
    private LocalDateTime createdAt;
}