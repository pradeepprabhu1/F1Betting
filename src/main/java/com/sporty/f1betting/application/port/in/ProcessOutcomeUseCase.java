package com.sporty.f1betting.application.port.in;

public interface ProcessOutcomeUseCase {
    void execute(Long sessionId, Integer winnerId);
}