package com.sporty.f1betting.adapter.out.external;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


import com.sporty.f1betting.domain.model.F1Session;

public class OpenF1AdapterTest {

    private OpenF1Adapter openF1Adapter;

    @Before
    public void setUp() {
        // Since RestTemplate is initialized internally and the API is simulated,
        // we can instantiate the class directly for unit testing.
        openF1Adapter = new OpenF1Adapter();
    }

    @Test
    public void shouldReturnSimulatedSessionWhenFiltersMatch() {
        // Act: Requesting the exact data present in the simulation
        List<F1Session> results = openF1Adapter.getSessions("Sprint Qualifying", 2023, "Belgium");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        
        F1Session session = results.get(0);
        assertEquals(Long.valueOf(9140L), session.sessionId());
        assertEquals("Belgium", session.countryName());
        assertEquals(2023, (int) session.year());
        
        // Verify Requirement: Markets should have random odds between 2 and 4
        assertFalse(session.markets().isEmpty());
        int odds = session.markets().get(0).odds();
        assertTrue("Odds should be between 2 and 4", odds >= 2 && odds <= 4);
    }

    @Test
    public void shouldReturnEmptyListWhenFiltersDoNotMatch() {
        // Act: Requesting a year that isn't in our simulation (2023 vs 2024)
        List<F1Session> results = openF1Adapter.getSessions("Race", 2024, "UK");

        // Assert
        assertTrue("Results should be empty for non-matching filters", results.isEmpty());
    }

    @Test
    public void shouldHandleNullFiltersAndReturnAllSimulatedData() {
        // Act: Passing nulls should bypass filters in your implementation
        List<F1Session> results = openF1Adapter.getSessions(null, null, null);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }
}