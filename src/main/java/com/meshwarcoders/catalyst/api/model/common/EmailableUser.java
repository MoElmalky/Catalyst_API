package com.meshwarcoders.catalyst.api.model.common;

public interface EmailableUser {
    String getEmail();
    boolean isEmailConfirmed();
    void setEmailConfirmed(boolean confirmed);
}
