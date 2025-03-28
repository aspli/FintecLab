package com.bootcamp.conta_service.service;

import com.bootcamp.conta_service.dto.ContaDTO;
import com.bootcamp.conta_service.dto.ContaRequestDTO;
import com.bootcamp.conta_service.dto.ContaResponseDTO;
import com.bootcamp.conta_service.exception.ContaExistenteException;
import com.bootcamp.conta_service.exception.ContaNaoExistenteException;
import com.bootcamp.conta_service.feign.BacenService;
import com.bootcamp.conta_service.model.Conta;
import com.bootcamp.conta_service.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContaService {
    private final ContaRepository contaRepository;
    private final BacenService bacenService;

    @Transactional
    public ContaResponseDTO criarConta(ContaRequestDTO contaRequestDTO) {
        Optional<Conta> contaOptional = contaRepository.findByNomeTitularAndNumeroContaAndChavePix(
                contaRequestDTO.getNomeTitular(),
                contaRequestDTO.getNumeroConta(),
                contaRequestDTO.getChavePix()
        );

        if (contaOptional.isPresent()) {
            throw new ContaExistenteException("Conta já existe.");
        }

        Conta conta = Conta.builder()
                .nomeTitular(contaRequestDTO.getNomeTitular())
                .numeroAgencia(contaRequestDTO.getNumeroAgencia())
                .numeroConta(contaRequestDTO.getNumeroConta())
                .chavePix(contaRequestDTO.getChavePix())
                .saldo(new BigDecimal(5000))
                .build();

        Conta contaSalva = contaRepository.save(conta);

        bacenService.criarChave(contaSalva.getChavePix());

        ContaResponseDTO contaResponseDTO = ContaResponseDTO.builder()
                .id(contaSalva.getId())
                .nomeTitular(contaRequestDTO.getNomeTitular())
                .build();

        log.info("ContaResponseDTO: {}", contaResponseDTO);


        return contaResponseDTO;
    }

    public List<ContaDTO> buscaTodasContas() {

        List<ContaDTO> contas = contaRepository.findAll().stream().map(
                        conta -> ContaDTO.builder()
                                .id(conta.getId())
                                .nomeTitular(conta.getNomeTitular())
                                .numeroAgencia(conta.getNumeroAgencia())
                                .numeroConta(conta.getNumeroConta())
                                .chavePix(conta.getChavePix())
                                .saldo(conta.getSaldo())
                                .build())
                .toList();

        return contas;

    }

    public ContaDTO buscaContaById(UUID id) {

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoExistenteException("Conta não existe."));

        ContaDTO contaDTO = ContaDTO.builder()
                .id(conta.getId())
                .nomeTitular(conta.getNomeTitular())
                .numeroAgencia(conta.getNumeroAgencia())
                .numeroConta(conta.getNumeroConta())
                .chavePix(conta.getChavePix())
                .saldo(conta.getSaldo())
                .build();

        return contaDTO;
    }

    public ContaResponseDTO atualizarConta(ContaRequestDTO contaRequestDTO, UUID id) {

        Conta contaExistente = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoExistenteException("Conta não existe."));

        contaExistente.setNomeTitular(contaRequestDTO.getNomeTitular());
        contaExistente.setNumeroConta(contaRequestDTO.getNumeroConta());
        contaExistente.setNumeroAgencia(contaRequestDTO.getNumeroAgencia());
        contaExistente.setChavePix(contaRequestDTO.getChavePix());

        contaExistente = contaRepository.save(contaExistente);

        return ContaResponseDTO.builder()
                .id(contaExistente.getId())
                .nomeTitular(contaExistente.getNomeTitular())
                .build();
    }

    public void deletarConta(UUID id) {
        contaRepository.findById(id).orElseThrow(() -> new ContaNaoExistenteException("Conta não existe."));

        contaRepository.deleteById(id);
    }
}
