package com.image.processing.api.repository;

import com.image.processing.api.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Integer> {
    List<Webhook> findByDeletedTs(LocalDateTime deletedTs);
}
