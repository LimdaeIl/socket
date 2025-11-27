package com.book.socket.domain.auth.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User를 생성 response")
public record CreateUserResponse(
        @Schema(description = "성공 유무")
        String code
) {


}
