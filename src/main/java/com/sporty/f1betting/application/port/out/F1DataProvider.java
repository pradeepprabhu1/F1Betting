package com.sporty.f1betting.application.port.out;

import java.util.List;

import com.sporty.f1betting.domain.model.F1Session;

public interface F1DataProvider {
    List<F1Session> getSessions(String type, Integer year, String country);
}
	