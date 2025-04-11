package com.generic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.generic.enumration.FormStatus;
import com.generic.enumration.FormType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "forms")
public class Forms {

    @Id
    private String id;
    private String title;
    private String description;
    private FormStatus status;
    private FormType formType;
    private Theme theme;

    private Map<String, Object> settings = new HashMap<>();
    private List<FormElement> elements = new ArrayList<>();
    private List<FormSection> sections = new ArrayList<>();
    private List<FormScript> scripts = new ArrayList<>();
    private String templateId;
    private boolean isTemplate;

    private String createdBy;

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @CreatedDate
    private Instant createdAt;

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @LastModifiedDate
    private Instant updatedAt;


    // Helper methods
    public void addElement(FormElement element) {
        elements.add(element);
        updateElementOrder();
    }

    public void removeElement(String elementId) {
        elements.removeIf(element -> element.getId().equals(elementId));
        updateElementOrder();
    }


    public void updateElementOrder() {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).setOrder(i + 1);
        }
    }

    /*
    public void addSection(FormSection section) {
        sections.add(section);
        updateSectionOrder();
    }

    public void updateSectionOrder() {
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setOrder(i + 1);
        }
    }*/
}
