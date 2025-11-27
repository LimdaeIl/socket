package com.book.socket.domain.auth.model.response;

import com.book.socket.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response")
public record LoginResponse(
        @Schema(description = "error code")
        ErrorCode description,

        @Schema(description = "jwt token")
        String token
) {

}
