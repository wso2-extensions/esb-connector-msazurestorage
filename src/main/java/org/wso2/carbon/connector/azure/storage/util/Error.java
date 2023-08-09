package org.wso2.carbon.connector.azure.storage.util;

public enum Error {

    CONNECTION_ERROR("500", "S3:CONNECTION_ERROR"),
    INVALID_CONFIGURATION("400", "S3:INVALID_CONFIGURATION"),
    BAD_REQUEST("400", "S3:BAD_REQUEST"),
    NOT_FOUND("404", "S3:NOT_FOUND"),
    CONFLICT("409", "S3:CONFLICT");

    private String code;
    private String message;

    /**
     * Create an error code.
     *
     * @param code    error code represented by number
     * @param message error message
     */
    Error(String code, String message) {

        this.code = code;
        this.message = message;
    }

    public String getErrorCode() {

        return this.code;
    }

    public String getErrorDetail() {

        return this.message;
    }
}
