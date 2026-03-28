package com.sporty.f1betting.adapter.in.web;

public record OutcomeRequest(
    Long sessionId,
    Integer winnerId
) {}