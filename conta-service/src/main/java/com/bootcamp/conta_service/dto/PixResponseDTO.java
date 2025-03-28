package com.bootcamp.conta_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PixResponseDTO {

    private LocalDateTime createdAt;
    private String message;
    private PixDTO pix;
}
