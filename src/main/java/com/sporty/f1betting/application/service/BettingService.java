package com.sporty.f1betting.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sporty.f1betting.adapter.out.persistence.BetEntity;
import com.sporty.f1betting.adapter.out.persistence.BetRepository;
import com.sporty.f1betting.adapter.out.persistence.UserEntity;
import com.sporty.f1betting.adapter.out.persistence.UserRepository;
import com.sporty.f1betting.application.port.in.GetEventsUseCase;
import com.sporty.f1betting.application.port.in.PlaceBetUseCase;
import com.sporty.f1betting.application.port.in.ProcessOutcomeUseCase;
import com.sporty.f1betting.application.port.out.F1DataProvider;
import com.sporty.f1betting.domain.exception.InsufficientBalanceException;
import com.sporty.f1betting.domain.exception.ResourceNotFoundException;
import com.sporty.f1betting.domain.model.BetStatus;
import com.sporty.f1betting.domain.model.F1Session;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BettingService implements GetEventsUseCase, PlaceBetUseCase, ProcessOutcomeUseCase {

    private final BetRepository betRepo;
    private final UserRepository userRepo;
    private final F1DataProvider f1DataProvider; // The Output Port

    @Override
    public List<F1Session> execute(String type, Integer year, String country) {
        return f1DataProvider.getSessions(type, year, country);
    }

    @Override
    @Transactional
    public BetEntity execute(Long userId, Long sessionId, Integer driverId, BigDecimal amount, Integer odds) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds. Balance: " + user.getBalance());
        }

        // Business Rule: Deduct balance immediately upon placing bet
        user.setBalance(user.getBalance().subtract(amount));
        userRepo.save(user);

        BetEntity bet = BetEntity.builder()
                .userId(userId)
                .sessionId(sessionId)
                .driverId(driverId)
                .amount(amount)
                .odds(odds)
                .status(BetStatus.PENDING)
                .build();
        
        return betRepo.save(bet);
    }

    @Override
    @Transactional
    public void execute(Long sessionId, Integer winnerId) {
        List<BetEntity> pendingBets = betRepo.findBySessionIdAndStatus(sessionId, BetStatus.PENDING);
        
        for (BetEntity bet : pendingBets) {
            if (bet.getDriverId().equals(winnerId)) {
                // Business Rule: Prize = amount * odds
                BigDecimal prize = bet.getAmount().multiply(BigDecimal.valueOf(bet.getOdds()));
                UserEntity user = userRepo.findById(bet.getUserId()).orElseThrow();
                user.setBalance(user.getBalance().add(prize));
                bet.setStatus(BetStatus.WON);
            } else {
                bet.setStatus(BetStatus.LOST);
            }
        }
        betRepo.saveAll(pendingBets);
    }
}