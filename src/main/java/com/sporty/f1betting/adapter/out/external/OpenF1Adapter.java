package com.sporty.f1betting.adapter.out.external;

import com.sporty.f1betting.application.port.out.F1DataProvider;
import com.sporty.f1betting.domain.model.DriverMarket;
import com.sporty.f1betting.domain.model.F1Session;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class OpenF1Adapter implements F1DataProvider {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://api.openf1.org/v1/sessions";

    @Override
    public List<F1Session> getSessions(String type, Integer year, String country) {
        // Build query string dynamically based on filters
        StringBuilder url = new StringBuilder(BASE_URL).append("?");
        if (type != null) url.append("session_type=").append(type).append("&");
        if (year != null) url.append("year=").append(year).append("&");
        if (country != null) url.append("country_name=").append(country).append("&");

        /* Commented out for Simulation since url required paid key creation to access it */
//        Map<String, Object>[] response = restTemplate.getForObject(url.toString(), Map[].class);
//
//        if (response == null) return Collections.emptyList();

     // SIMULATED RESPONSE
        return simulateApiResponse().stream()
                .filter(s -> (type == null || type.equals(s.get("session_type"))))
                .filter(s -> (year == null || year.equals(s.get("year"))))
                .filter(s -> (country == null || country.equals(s.get("country_name"))))
                .map(this::mapToDomain)
                .toList();
    }
    
    private List<Map<String, Object>> simulateApiResponse() {
        Map<String, Object> session = new HashMap<>();
        session.put("session_key", 9140L);
        session.put("session_name", "Sprint Qualifying");
        session.put("country_name", "Belgium");
        session.put("year", 2023);
        session.put("session_type", "Sprint Qualifying");
        
        return List.of(session);
    }

    private F1Session mapToDomain(Map<String, Object> map) {
        // Requirement: Odds must be random integers 2, 3, or 4
        List<DriverMarket> randomMarkets = List.of(
            new DriverMarket(1, "Max Verstappen", ThreadLocalRandom.current().nextInt(2, 5)),
            new DriverMarket(44, "Lewis Hamilton", ThreadLocalRandom.current().nextInt(2, 5)),
            new DriverMarket(16, "Charles Leclerc", ThreadLocalRandom.current().nextInt(2, 5))
        );

        return new F1Session(
            Long.valueOf(map.get("session_key").toString()),
            (String) map.get("session_name"),
            (String) map.get("country_name"),
            (Integer) map.get("year"),
            (String) map.get("session_type"),
            randomMarkets
        );
    }
}