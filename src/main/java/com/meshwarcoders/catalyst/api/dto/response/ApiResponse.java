package com.meshwarcoders.catalyst.api.dto.response;

public record ApiResponse (boolean success,
        String message,
        Object data) {
    public ApiResponse(boolean success, String message){
        this(success, message, null);
    }
}
