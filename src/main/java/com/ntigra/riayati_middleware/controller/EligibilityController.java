package com.ntigra.riayati_middleware.controller;


import com.ntigra.riayati_middleware.dto.request.eligibility.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eligibility")
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService service;

    @PostMapping
    public ResponseEntity<?> eligibility(
            @RequestBody EligibilityRequestDto request) {

        return ResponseEntity.ok(
                service.submit(request));
    }
}