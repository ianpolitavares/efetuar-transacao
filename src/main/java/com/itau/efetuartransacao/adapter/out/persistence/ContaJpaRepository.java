package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.adapter.out.persistence.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaJpaRepository extends JpaRepository<ContaEntity, String> {
}
