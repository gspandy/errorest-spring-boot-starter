package com.github.mkopylec.errorest.handling;

import com.github.mkopylec.errorest.handling.providers.ErrorData;
import com.github.mkopylec.errorest.handling.providers.ErrorDataProvider;
import com.github.mkopylec.errorest.handling.providers.ErrorDataProviderContext;
import com.github.mkopylec.errorest.logging.ExceptionLogger;
import com.github.mkopylec.errorest.response.Errors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class ControllerErrorHandler {

    protected final ExceptionLogger logger;
    protected final ErrorDataProviderContext providerContext;

    public ControllerErrorHandler(ExceptionLogger logger, ErrorDataProviderContext providerContext) {
        this.logger = logger;
        this.providerContext = providerContext;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Errors> handleThrowable(Throwable ex, HttpServletRequest request) {
        ErrorData errorData = getErrorData(ex, request);
        logger.log(errorData);
        Errors errors = new Errors(errorData.getErrors());
        return status(errorData.getResponseStatus())
                .body(errors);
    }

    @SuppressWarnings("unchecked")
    protected ErrorData getErrorData(Throwable ex, HttpServletRequest request) {
        ErrorDataProvider provider = providerContext.getErrorDataProvider(ex);
        return provider.getErrorData(ex, request);
    }
}
