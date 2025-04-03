package com.generic.model;

import com.generic.enumration.SubmissionSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionMetadata {
    private String ip;
    private String userAgent;
    private int completionTime;
    private SubmissionSource source;
    private String referer;
}
