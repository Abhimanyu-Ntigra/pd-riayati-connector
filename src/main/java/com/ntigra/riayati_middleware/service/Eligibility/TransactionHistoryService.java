package com.ntigra.riayati_middleware.service.Eligibility;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntigra.riayati_middleware.entity.TransactionHistory;

import com.ntigra.riayati_middleware.respository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final ObjectMapper mapper;
    private final TransactionHistoryRepository repository;

    public void log(
            String type,
            Object request,
            Object response) {

        try {

            TransactionHistory history =
                    new TransactionHistory();

            history.setTransactionType(type);

            history.setRequestPayload(
                    mapper.writeValueAsString(request));

            history.setResponsePayload(
                    mapper.writeValueAsString(response));

            history.setCreatedAt(
                    LocalDateTime.now());

            repository.save(history);

        } catch (Exception ignored) {
        }
    }
}