package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.adapter.out.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, UUID> {
}
