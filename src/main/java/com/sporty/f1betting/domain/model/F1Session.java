package com.sporty.f1betting.domain.model;

import java.util.List;

public record F1Session(Long sessionId, String sessionName, String countryName, 
		Integer year, String sessionType, List<DriverMarket> markets) {}

