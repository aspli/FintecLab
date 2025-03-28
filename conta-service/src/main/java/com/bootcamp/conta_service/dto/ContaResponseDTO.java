package com.bootcamp.conta_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ContaResponseDTO {
    private UUID id;
    private String nomeTitular;
}
