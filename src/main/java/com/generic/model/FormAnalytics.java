package com.generic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "form_analytics")
public class FormAnalytics {
    @Id
    private String id;
    private String formId;
    private int viewCount;
    private int submissionCount;
    private int abandonmentCount;
    private Map<String, Object> metricData;
    private LocalDateTime lastUpdated;
}
