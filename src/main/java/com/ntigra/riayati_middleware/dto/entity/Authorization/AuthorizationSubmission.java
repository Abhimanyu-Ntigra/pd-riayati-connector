package com.ntigra.riayati_middleware.dto.entity.Authorization;

import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationSubmission {
    private Header header;
    private Authorization authorization;
}