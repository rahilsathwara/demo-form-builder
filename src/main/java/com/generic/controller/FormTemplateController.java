package com.generic.controller;

import com.generic.enumration.TemplateCategory;
import com.generic.model.FormTemplate;
import com.generic.service.FormTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/form-templates")
@RequiredArgsConstructor
public class FormTemplateController {

    private final FormTemplateService templateService;

    @GetMapping("/{templateId}")
    public Mono<FormTemplate> getTemplate(@PathVariable("templateId") String templateId) {
        return templateService.getTemplateById(templateId);
    }

    @GetMapping("/category/{category}")
    public Flux<FormTemplate> getTemplatesByCategory(@PathVariable("category") TemplateCategory category) {
        return templateService.getTemplatesByCategory(category);
    }

    @GetMapping("/system")
    public Flux<FormTemplate> getSystemTemplates() {
        return templateService.getSystemTemplates();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FormTemplate> createTemplate(@RequestBody FormTemplate template) {
        return templateService.createTemplate(template);
    }

    @PutMapping("/{templateId}")
    public Mono<FormTemplate> updateTemplate(@PathVariable("templateId") String templateId, @RequestBody FormTemplate template) {
        return templateService.updateTemplate(templateId, template);
    }

    @PutMapping("/{templateId}/increment-usage")
    public Mono<FormTemplate> incrementUsage(@PathVariable("templateId") String templateId) {
        return templateService.incrementUsageCount(templateId);
    }

    @DeleteMapping("/{templateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTemplate(@PathVariable("templateId") String templateId) {
        return templateService.deleteTemplate(templateId);
    }
}
