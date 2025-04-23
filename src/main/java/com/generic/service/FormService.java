package com.generic.service;

import com.generic.enumration.FormStatus;
import com.generic.model.FormElement;
import com.generic.model.Forms;
import com.generic.payload.request.FormRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface FormService {

    Mono<Forms> createForm(Forms form);
    Flux<Forms> getAllForms();
    Mono<Forms> getFormById(String id);
    Mono<Forms> getPublishedForm(String id);
    Flux<Forms> getFormsByStatus(FormStatus status);
    Flux<Forms> getFormsByUser(String userId);
    Mono<Forms> updateForm(String id, Forms formUpdates);
    Mono<Forms> changeFormStatus(String id, FormStatus status);
    Mono<Forms> addElement(String formId, FormElement element);
    Mono<Forms> removeElement(String formId, String elementId);
    Mono<Void> deleteForm(String id);
}
