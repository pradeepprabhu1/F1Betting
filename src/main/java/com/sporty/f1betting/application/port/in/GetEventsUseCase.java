package com.sporty.f1betting.application.port.in;

import java.util.List;

import com.sporty.f1betting.domain.model.F1Session;

public interface GetEventsUseCase {
    List<F1Session> execute(String type, Integer year, String country);
}