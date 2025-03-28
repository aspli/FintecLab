package com.bootcamp.conta_service.service;

import com.bootcamp.conta_service.dto.PixDTO;
import com.bootcamp.conta_service.dto.PixRequestDTO;
import com.bootcamp.conta_service.dto.PixResponseDTO;
import com.bootcamp.conta_service.exception.ContaNaoExistenteException;
import com.bootcamp.conta_service.exception.SaldoInsuficienteException;
import com.bootcamp.conta_service.feign.BacenService;
import com.bootcamp.conta_service.model.Conta;
import com.bootcamp.conta_service.model.Pix;
import com.bootcamp.conta_service.repository.ContaRepository;
import com.bootcamp.conta_service.repository.PixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PixService {

    private final PixRepository pixRepository;
    private final ContaRepository contaRepository;
    private final BacenService bacenService;

    @Transactional
    public PixResponseDTO realizaPix(PixRequestDTO pixRequestDTO) {

        if (pixRequestDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Nao pode fazer pix negativo");
        }

        Optional<Pix> existingPix = pixRepository.findByIdempotencia(pixRequestDTO.getIdempotencia());

        if (existingPix.isPresent()) {
            return new PixResponseDTO(
                    existingPix.get().getCreatedAt(),
                    "Pix já processado com sucesso (idempotente).",
                    existingPix.map(this::entityToDto).get()
            );
        }

        String chavePagador = bacenService.buscaChave(pixRequestDTO.getChavePixPagador()).getChave();

        Optional<Conta> contaPagadorOptional = contaRepository.findByChavePix(chavePagador);

        if (contaPagadorOptional.isEmpty()) {
            throw new ContaNaoExistenteException(String.format("Conta com a chave %s não existe", pixRequestDTO.getChavePixPagador()));
        }

        String chaveRecebedor = bacenService.buscaChave(pixRequestDTO.getChavePixRecebedor()).getChave();

        Optional<Conta> contaRecebedorOptional = contaRepository.findByChavePix(chaveRecebedor);

        if (contaRecebedorOptional.isEmpty()) {
            throw new ContaNaoExistenteException(String.format("Conta com a chave %s não existe", pixRequestDTO.getChavePixPagador()));
        }

        Conta contaPagador = contaPagadorOptional.get();
        Conta contaRecebedor = contaRecebedorOptional.get();

        //Pix Valor 201
        //Saldo Conta 200
        //valor maior que o saldo = Retorna maior que 0
        //Valor for Igual ao Saldo = Retorna igual a 0
        //Valor for menor que o Saldo = Retorna menor que 0
        if (pixRequestDTO.getValor().compareTo(contaPagador.getSaldo()) > 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o Pix");
        }

        contaPagador.sacar(pixRequestDTO.getValor());
        contaRecebedor.depositar(pixRequestDTO.getValor());

        contaRepository.save(contaPagador);
        contaRepository.save(contaRecebedor);

        Pix pix = Pix.builder()
                .chavePixPagador(pixRequestDTO.getChavePixPagador())
                .chavePixRecebedor(pixRequestDTO.getChavePixRecebedor())
                .conta(contaPagador)
                .valor(pixRequestDTO.getValor())
                .idempotencia(pixRequestDTO.getIdempotencia())
                .createdAt(LocalDateTime.now())
                .build();

        pixRepository.save(pix);

        return PixResponseDTO.builder()
                .pix(entityToDto(pix))
                .createdAt(pix.getCreatedAt())
                .message("Pix Realizado com sucesso")
                .build();
    }

    private PixDTO entityToDto(Pix pix) {
        return PixDTO.builder()
                .id(pix.getId())
                .chavePixPagador(pix.getChavePixPagador())
                .chavePixRecebedor(pix.getChavePixRecebedor())
                .valor(pix.getValor())
                .createdAt(pix.getCreatedAt())
                .idempotencia(pix.getIdempotencia())
                .build();
    }
}
