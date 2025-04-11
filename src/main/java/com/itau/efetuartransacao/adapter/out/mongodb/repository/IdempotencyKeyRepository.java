package com.itau.efetuartransacao.adapter.out.mongodb.repository;

import com.itau.efetuartransacao.adapter.out.mongodb.entity.IdempotencyKey;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdempotencyKeyRepository extends MongoRepository<IdempotencyKey, String> {
}
