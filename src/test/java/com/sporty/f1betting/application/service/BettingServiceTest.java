package com.sporty.f1betting.application.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sporty.f1betting.adapter.out.persistence.BetEntity;
import com.sporty.f1betting.adapter.out.persistence.BetRepository;
import com.sporty.f1betting.adapter.out.persistence.UserEntity;
import com.sporty.f1betting.adapter.out.persistence.UserRepository;
import com.sporty.f1betting.application.port.out.F1DataProvider;
import com.sporty.f1betting.domain.exception.InsufficientBalanceException;
import com.sporty.f1betting.domain.model.BetStatus;

public class BettingServiceTest {

    @Mock
    private BetRepository betRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private F1DataProvider f1DataProvider;

    @InjectMocks
    private BettingService bettingService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPlaceBet_Success() {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, new BigDecimal("100.00"));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        bettingService.execute(userId, 9140L, 1, new BigDecimal("20.00"), 3);

        // Assert
        assertEquals(new BigDecimal("80.00"), user.getBalance());
        verify(userRepo, times(1)).save(user);
        verify(betRepo, times(1)).save(any(BetEntity.class));
    }

    @Test(expected = InsufficientBalanceException.class)
    public void testPlaceBet_InsufficientBalance() {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity(userId, new BigDecimal("10.00"));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert (via Expected Exception)
        bettingService.execute(userId, 9140L, 1, new BigDecimal("50.00"), 3);
    }

    @Test
    public void testProcessOutcome_WinningBetUpdatesBalance() {
        // Arrange
        Long sessionId = 9140L;
        Integer winnerId = 1;
        
        UserEntity user = new UserEntity(1L, new BigDecimal("80.00"));
        BetEntity winningBet = BetEntity.builder()
                .userId(1L).sessionId(sessionId).driverId(1).amount(new BigDecimal("20.00")).odds(3).status(BetStatus.PENDING).build();
        BetEntity losingBet = BetEntity.builder()
                .userId(1L).sessionId(sessionId).driverId(44).amount(new BigDecimal("10.00")).odds(2).status(BetStatus.PENDING).build();

        when(betRepo.findBySessionIdAndStatus(sessionId, BetStatus.PENDING))
                .thenReturn(Arrays.asList(winningBet, losingBet));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        // Act
        bettingService.execute(sessionId, winnerId);

        // Assert
        // Winning calculation: 80 + (20 * 3) = 140
        assertEquals(new BigDecimal("140.00"), user.getBalance());
        assertEquals(BetStatus.WON, winningBet.getStatus());
        assertEquals(BetStatus.LOST, losingBet.getStatus());
        verify(betRepo).saveAll(anyList());
    }
}