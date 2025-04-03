package com.generic.model;

import com.generic.enumration.SubmissionStatus;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "form_submissions")
public class FormSubmission {
    @Id
    private String id;
    private String formId;
    private SubmissionStatus status;
    private Map<String, Object> data;
    private SubmissionMetadata metadata;

    private String submittedBy;
    private LocalDateTime submittedAt;
}
