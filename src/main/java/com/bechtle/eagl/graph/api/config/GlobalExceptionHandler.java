package com.bechtle.eagl.graph.api.config;

import com.bechtle.eagl.graph.domain.model.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

/**
 * @see <a href="https://github.com/Opalo/spring-webflux-and-domain-exceptions/blob/master/error-attributes/src/test/java/org/opal/DomainExceptionWrapper.java">Description</a>
 */
@Configuration
@Slf4j
public class GlobalExceptionHandler extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {

        Throwable error = super.getError(request);
        printWarning(error);

        options = options.including(ErrorAttributeOptions.Include.BINDING_ERRORS)
                .including(ErrorAttributeOptions.Include.MESSAGE)
                .including(ErrorAttributeOptions.Include.EXCEPTION)
                .including(ErrorAttributeOptions.Include.STACK_TRACE);

        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        if (error instanceof IllegalArgumentException) {
            errorAttributes.replace("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        } else if (error instanceof EntityNotFound) {
            errorAttributes.replace("status", HttpStatus.NOT_FOUND.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        } else if (error instanceof MissingType) {
            errorAttributes.replace("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        } else if (error instanceof InvalidEntityModel) {
            errorAttributes.replace("status", HttpStatus.CONFLICT.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        } else if (error instanceof EntityExistsAlready) {
            errorAttributes.replace("status", HttpStatus.CONFLICT.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        } else if (error instanceof UnknownPrefix) {
            errorAttributes.replace("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        } else if (error instanceof RDFParseException) {
            errorAttributes.replace("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.put("line", ((RDFParseException) error).getLineNumber());
            errorAttributes.put("column", ((RDFParseException) error).getColumnNumber());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");

        } else if (error instanceof MalformedQueryException) {
            errorAttributes.replace("status", HttpStatus.BAD_REQUEST.value());
            errorAttributes.replace("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorAttributes.put("reason", ((MalformedQueryException) error).getMessage());
            errorAttributes.remove("exception");
            errorAttributes.remove("trace");
        }


        return errorAttributes;
    }

    private void printWarning(Throwable error) {
        log.warn("(ErrorHandler) Handling error '{}' with message: {}", error.getClass().getSimpleName(), error.getMessage());
    }
}
