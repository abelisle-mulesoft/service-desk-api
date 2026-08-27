package com.brilliantmule.servicedesk.error;

import com.brilliantmule.servicedesk.incident.error.IncidentNotFoundException;
import com.brilliantmule.servicedesk.incident.error.IncidentStateConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;

/**
 * Central exception handler for the Service Desk API.
 * <p>
 * Applies to all {@code @RestController} endpoints and converts application
 * exceptions into RFC 7807 {@link ProblemDetail} responses with appropriate
 * HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link IncidentNotFoundException} by returning a {@code 404 Not Found} response.
     * <p>
     * The response title is {@code "Incident Not Found"} and the detail contains
     * the exception message.
     *
     * @param exception the exception thrown when an incident identifier does not exist
     * @return problem details describing the missing incident
     */
    @ExceptionHandler(IncidentNotFoundException.class)
    public ProblemDetail handleIncidentNotFound(
            IncidentNotFoundException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setTitle("Incident Not Found");

        return problem;
    }

    /**
     * Handles {@link IncidentStateConflictException} by returning a
     * {@code 409 Conflict} response.
     *
     * @param exception the exception thrown when an incident operation conflicts
     *                  with the incident's current lifecycle state
     * @return problem details describing the state conflict
     */
    @ExceptionHandler(IncidentStateConflictException.class)
    public ProblemDetail handleIncidentStateConflict(
            IncidentStateConflictException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problem.setTitle("Invalid Incident State");

        return problem;
    }

    /**
     * Handles {@link MethodArgumentNotValidException} by returning a {@code 400 Bad Request} response.
     * <p>
     * Field-level validation errors are included in an {@code errors} property as
     * {@code "fieldName: message"} strings. The response title is {@code "Validation Failed"}.
     *
     * @param exception the exception thrown when a {@code @Valid} request body fails validation
     * @return problem details describing the validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError(
            MethodArgumentNotValidException exception) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": "
                                + error.getDefaultMessage())
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "The request contains invalid data."
        );

        problem.setTitle("Validation Failed");
        problem.setProperty("errors", errors);

        return problem;
    }

    /**
     * Handles invalid request parameter values, such as unsupported enum values.
     *
     * @param exception the request parameter type-mismatch exception
     * @return problem details describing the invalid request parameter
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value '" + exception.getValue()
                        + "' for parameter '" + exception.getName() + "'."
        );

        problem.setTitle("Invalid Request Parameter");

        Class<?> requiredType = exception.getRequiredType();
        if (requiredType != null && requiredType.isEnum()) {
            List<String> allowedValues = Arrays.stream(requiredType.getEnumConstants())
                    .map(Object::toString)
                    .toList();

            problem.setProperty("allowedValues", allowedValues);
        }

        return problem;
    }

    /**
     * Handles request bodies that cannot be deserialized, including malformed JSON
     * and unsupported enum values.
     *
     * @param exception the unreadable request-body exception
     * @return problem details describing the invalid request body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "The request body could not be read. Check the JSON syntax and supplied values."
        );

        problem.setTitle("Invalid Request Body");

        return problem;
    }
}
