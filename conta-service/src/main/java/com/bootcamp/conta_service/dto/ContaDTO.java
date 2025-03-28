package com.bootcamp.conta_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class ContaDTO {

    private UUID id;
    private String nomeTitular;
    private Integer numeroAgencia;
    private Integer numeroConta;
    private String chavePix;
    private BigDecimal saldo;

}
