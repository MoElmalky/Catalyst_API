package com.meshwarcoders.catalyst.api.dto;

import jakarta.validation.constraints.NotNull;

public class HandleRequestDTO {
    @NotNull(message = "Request ID is required")
    private Long requestId;

    @NotNull(message = "Action is required (APPROVE or REJECT)")
    private String action;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
