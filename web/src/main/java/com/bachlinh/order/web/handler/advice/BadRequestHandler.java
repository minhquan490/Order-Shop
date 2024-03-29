package com.bachlinh.order.web.handler.advice;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteExceptionHandler;
import com.bachlinh.order.http.NativeResponse;
import com.bachlinh.order.http.handler.ExceptionHandler;
import com.bachlinh.order.core.exception.http.BadVariableException;
import com.bachlinh.order.core.exception.http.InvalidTokenException;
import com.bachlinh.order.core.exception.http.ValidationFailureException;
import com.bachlinh.order.http.handler.ThrowableHandler;

import org.springframework.http.HttpStatus;

@ActiveReflection
@RouteExceptionHandler
public class BadRequestHandler extends ExceptionHandler {
    private final CurlyBracketProcessor curlyBracketProcessor = new CurlyBracketProcessor();
    private final SquareBracketProcessor squareBracketProcessor = new SquareBracketProcessor();
    private final CommaProcessor commaProcessor = new CommaProcessor();

    private BadRequestHandler() {}

    @Override
    protected int status() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    protected String[] message(Exception exception) {
        String returnMessage = exception.getMessage();
        String processedMsg = curlyBracketProcessor.process(returnMessage);
        processedMsg = squareBracketProcessor.process(processedMsg);
        return commaProcessor.process(processedMsg);
    }

    @Override
    protected void doOnException(Exception exception) {
        // Do nothing
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Exception>[] activeOnTypes() {
        return new Class[]{
                BadVariableException.class,
                InvalidTokenException.class,
                ValidationFailureException.class
        };
    }

    @Override
    public boolean isErrorHandler() {
        return false;
    }

    @Override
    public ThrowableHandler<Exception, NativeResponse<byte[]>> newInstance() {
        return new BadRequestHandler();
    }

    private static class SquareBracketProcessor {
        private static final String LEFT_SQUARE_BRACKET = "[";
        private static final String RIGHT_SQUARE_BRACKET = "]";

        public String process(String input) {
            if (input.startsWith(LEFT_SQUARE_BRACKET) && input.endsWith(RIGHT_SQUARE_BRACKET)) {
                return input.replace(LEFT_SQUARE_BRACKET, "").replace(RIGHT_SQUARE_BRACKET, "");
            } else {
                return input;
            }
        }
    }

    private static class CommaProcessor {
        private static final String COMMA = ",";

        public String[] process(String input) {
            if (input.contains(COMMA)) {
                return input.split(COMMA);
            } else {
                return new String[]{input};
            }
        }
    }

    private static class CurlyBracketProcessor {
        private static final String LEFT_CURLY_BRACKET = "{";
        private static final String RIGHT_CURLY_BRACKET = "}";

        public String process(String input) {
            if (input.startsWith(LEFT_CURLY_BRACKET) && input.endsWith(RIGHT_CURLY_BRACKET)) {
                return input.replace(LEFT_CURLY_BRACKET, "").replace(RIGHT_CURLY_BRACKET, "");
            } else {
                return input;
            }
        }
    }
}
