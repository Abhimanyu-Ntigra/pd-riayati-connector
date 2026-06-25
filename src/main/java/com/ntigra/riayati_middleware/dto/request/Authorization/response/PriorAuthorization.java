package com.ntigra.riayati_middleware.dto.request.Authorization.response;

import lombok.Data;

@Data
public class PriorAuthorization {

    private Header header;
    private Authorization authorization;
}
