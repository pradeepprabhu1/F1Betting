package com.sporty.f1betting.application.port.in;

import com.sporty.f1betting.adapter.out.persistence.BetEntity;
import java.math.BigDecimal;

public interface PlaceBetUseCase {
    BetEntity execute(Long userId, Long sessionId, Integer driverId, BigDecimal amount, Integer odds);
}