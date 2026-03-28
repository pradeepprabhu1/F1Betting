package com.sporty.f1betting.adapter.in.web;

import java.math.BigDecimal;

public record BetRequest(
    Long userId,
    Long sessionId,
    Integer driverId,
    BigDecimal amount,
    Integer odds
) {}