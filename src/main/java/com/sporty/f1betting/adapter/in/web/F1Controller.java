package com.sporty.f1betting.adapter.in.web;

import com.sporty.f1betting.application.port.in.*;
import com.sporty.f1betting.domain.model.F1Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class F1Controller {

    private final GetEventsUseCase getEventsUseCase;
    private final PlaceBetUseCase placeBetUseCase;
    private final ProcessOutcomeUseCase processOutcomeUseCase;

    @GetMapping("/events")
    public List<F1Session> listEvents(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country) {
    	return getEventsUseCase.execute(type, year, country);
    }

    @PostMapping("/bets")
    public ResponseEntity<String> placeBet(@RequestBody BetRequest req) {
        placeBetUseCase.execute(req.userId(), req.sessionId(), req.driverId(), req.amount(), req.odds());
        return ResponseEntity.ok("Bet placed successfully");
    }

    @PostMapping("/simulate-outcome")
    public ResponseEntity<String> simulate(@RequestBody OutcomeRequest req) {
        processOutcomeUseCase.execute(req.sessionId(), req.winnerId());
        return ResponseEntity.ok("Event outcome processed and balances updated");
    }
}