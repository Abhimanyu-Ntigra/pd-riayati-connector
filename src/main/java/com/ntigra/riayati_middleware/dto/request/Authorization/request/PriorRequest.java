package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;

@Data
public class PriorRequest {
    private RequestHeader header;
    private Authorization authorization;
}