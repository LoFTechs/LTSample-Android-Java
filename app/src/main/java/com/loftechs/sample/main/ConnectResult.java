package com.loftechs.sample.main;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class ConnectResult {

    @Nullable
    private Boolean success;
    @Nullable
    private Type type;
    @Nullable
    private Integer error;

    ConnectResult(@Nullable Integer error) {
        this.error = error;
    }

    ConnectResult(@Nullable Boolean success, @Nullable Type type) {
        this.success = success;
        this.type = type;
    }

    @Nullable
    Boolean getSuccess() {
        return success;
    }

    @Nullable
    Type getType() {
        return type;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}

enum Type {
    LOGIN,
    LOGOUT
}
