package com.generic.controller;

import com.generic.enumration.FormStatus;
import com.generic.model.FormElement;
import com.generic.model.Forms;
import com.generic.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @GetMapping("/{formId}")
    public Mono<Forms> getForm(@PathVariable("formId") String formId) {
        return formService.getFormById(formId);
    }

    @GetMapping
    public Flux<Forms> getAllForms(@RequestParam(required = false) FormStatus status,
            @RequestParam(required = false) String userId) {

        if (status != null) {
            return formService.getFormsByStatus(status);
        } else if (userId != null) {
            return formService.getFormsByUser(userId);
        } else {
            // This would typically be admin only and paginated
            return Flux.empty(); // For safety, don't return all forms by default
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Forms> createForm(@RequestBody Forms form) {
        return formService.createForm(form);
    }

    @PutMapping("/{formId}")
    public Mono<Forms> updateForm(@PathVariable("formId") String formId, @RequestBody Forms form) {
        return formService.updateForm(formId, form);
    }

    @DeleteMapping("/{formId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteForm(@PathVariable("formId") String formId) {
        return formService.deleteForm(formId);
    }

    @PostMapping("/{formId}/elements")
    public Mono<Forms> addElement(@PathVariable("formId") String formId, @RequestBody FormElement element) {
        return formService.addElement(formId, element);
    }

    @DeleteMapping("/{formId}/elements/{elementId}")
    public Mono<Forms> removeElement(@PathVariable("formId") String formId, @PathVariable("elementId") String elementId) {
        return formService.removeElement(formId, elementId);
    }

    // SSE endpoint for real-time form updates (for collaborative editing)
    @GetMapping(value = "/{formId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Forms> streamFormUpdates(@PathVariable("formId") String formId) {
        // This would be implemented using Kafka consumer or WebSocket
        // For simplicity, we're just returning the form here
        return Mono.just(formId)
                .flatMapMany(id -> formService.getFormById(id)
                        .flux()
                );
    }
}
