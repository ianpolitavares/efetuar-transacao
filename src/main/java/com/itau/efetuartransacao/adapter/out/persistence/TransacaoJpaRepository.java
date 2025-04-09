package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.adapter.out.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, String> {
}
