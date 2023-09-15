package com.challenge.viapath.error.handle;

import com.challenge.viapath.error.exception.BadRequestException;
import com.challenge.viapath.error.exception.NotFoundException;
import com.challenge.viapath.error.exception.ServerErrorException;
import com.challenge.viapath.error.exception.UnknownErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.rmi.UnexpectedException;

@Component
public class RestTemplateResponseErrorHandler extends Exception implements ResponseErrorHandler {

    Logger logger = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {

        return (clientHttpResponse.getStatusCode().is4xxClientError() || clientHttpResponse.getStatusCode().is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        HttpStatus statusCode = (HttpStatus) clientHttpResponse.getStatusCode();
        logger.warn("An error has occurred with status code: " + statusCode + " and message: " + clientHttpResponse.getStatusText());
        if (statusCode.is5xxServerError()) {
            throw new ServerErrorException("A server error has occurred" + statusCode);
        } else if (statusCode.is4xxClientError()) {
            switch (statusCode) {
                case NOT_FOUND -> throw new NotFoundException("Your request could not be found");
                case BAD_REQUEST -> throw new BadRequestException("Please check your request and error was found");
                case PAYMENT_REQUIRED -> throw new UnexpectedException("A Payment Required error has occurred, a new key must be generated");
                default -> throw new UnknownErrorException("An error has occurred, please contact support");
            }
        }
    }

}
