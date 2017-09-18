package com.acamica.social.rest;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.validation.ConstraintViolation;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.BindingResult;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class ErrorsBody {
    private final List<String> errors;

    public ErrorsBody(List<String> errors) {
        this.errors = errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getErrors() {
        return unmodifiableList(errors);
    }

    public static class Builder {
        private final ResourceBundleMessageSource messageSource;
        private final Locale locale;

        private Builder() {
            messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("validation-messages");

            locale = Locale.getDefault();
        }

        public ErrorsBody toResource(BindingResult bindingResult) {
            requireNonNull(bindingResult, "Can't build from a response without validation results");

            List<String> errors = bindingResult.getAllErrors()
                                               .stream()
                                               .map(e -> getErrorMessage(e.getDefaultMessage(), e.getArguments()))
                                               .collect(toList());

            return new ErrorsBody(errors);
        }

        public ErrorsBody toResource(String... messages) {
            requireNonNull(messages, "Can't build from a response without error messages");
            return new ErrorsBody(asList(messages));
        }

        public ErrorsBody toResource(Set<ConstraintViolation<?>> violations) {
            requireNonNull(violations, "Can't build from null violations");

            List<String> messages = violations.stream()
                                              .map(v -> getErrorMessage(v.getMessage(), v.getExecutableParameters()))
                                              .collect(toList());

            return new ErrorsBody(messages);
        }

        private String getErrorMessage(String message, Object[] executableParameters) {
            try {
                return messageSource.getMessage(message, executableParameters, locale);
            } catch (NoSuchMessageException e) {
                return message;
            }
        }
    }
}
