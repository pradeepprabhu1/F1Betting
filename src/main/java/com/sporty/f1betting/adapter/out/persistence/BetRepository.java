package com.sporty.f1betting.adapter.out.persistence;

import com.sporty.f1betting.domain.model.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<BetEntity, Long> {
    List<BetEntity> findBySessionIdAndStatus(Long sessionId, BetStatus status);
}