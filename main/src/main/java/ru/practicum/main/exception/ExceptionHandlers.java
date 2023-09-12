package ru.practicum.main.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e, WebRequest request) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason("Object not found " + request.getDescription(false))
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final BadRequestException e) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason("Request is not correctly")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final BadRequestException e) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason("Request is not correctly")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final BadRequestException e) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason("Request is not correctly")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final DataIntegrityViolationException e) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason("The required object was found.")
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleValidationException(final ForbiddenException e, WebRequest request) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason(request.getDescription(false))
                .status(HttpStatus.FORBIDDEN)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerErrorException(final HttpServerErrorException.InternalServerError e, WebRequest request) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason(request.getDescription(false))
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleThrowableExceptions(final MissingServletRequestParameterException e) {
        return new ApiError.ApiErrorBuilder()
                .message(e.getLocalizedMessage())
                .reason("Throwable exception")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now()).build();
    }
}
