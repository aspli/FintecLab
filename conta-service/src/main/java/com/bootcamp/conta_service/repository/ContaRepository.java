package com.bootcamp.conta_service.repository;

import com.bootcamp.conta_service.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {
    Optional<Conta> findByChavePix(String chavePixPagador);

    Optional<Conta> findByNomeTitularAndNumeroContaAndChavePix(String nomeTitular, Integer numeroConta, String chavePix);

}
