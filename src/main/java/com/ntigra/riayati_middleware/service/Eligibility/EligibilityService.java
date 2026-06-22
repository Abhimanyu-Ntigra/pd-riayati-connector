package com.ntigra.riayati_middleware.service.Eligibility;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;

import com.ntigra.riayati_middleware.dto.request.eligibility.request.EligibilityRequest;
import com.ntigra.riayati_middleware.dto.request.eligibility.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.mapper.eligibility.EligibilityRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final EligibilityRequestMapper mapper;
    private final RiayatiRestClient client;

    public String submit(
            EligibilityRequestDto dto) {

        EligibilityRequest request =
                mapper.map(dto);

        return client.submitEligibility(request);
    }
}