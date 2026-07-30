package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SleepTrackerAppTest {

    private List<SleepingSession> sampleSessions;

    @BeforeEach
    void setUp() {
        sampleSessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 15),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 50),
                        LocalDateTime.of(2025, 10, 3, 6, 40),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 10),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 40),
                        LocalDateTime.of(2025, 10, 4, 8, 0),
                        SleepQuality.BAD
                )
        );
    }

    @Test
    void testTotalSessions_CorrectCount() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult result = function.apply(sampleSessions);
        assertEquals(4, result.getValue());
    }

    @Test
    void testTotalSessions_EmptyList() {
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult result = function.apply(Collections.emptyList());
        assertEquals(0, result.getValue());
    }

    @Test
    void testMinDuration() {
        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult result = function.apply(sampleSessions);
        assertEquals(50L, result.getValue());
    }

    @Test
    void testMaxDuration() {
        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult result = function.apply(sampleSessions);
        assertEquals(495L, result.getValue());
    }

    @Test
    void testBadQualityCount() {
        BadQualityCountFunction function = new BadQualityCountFunction();
        SleepAnalysisResult result = function.apply(sampleSessions);
        assertEquals(1L, result.getValue());
    }

    @Test
    void testSleeplessNights_NoSleepless() {
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult result = function.apply(sampleSessions);
        assertEquals(0L, result.getValue());
    }

    @Test
    void testSleeplessNights_WithDaySleepOnly() {
        List<SleepingSession> dayOnly = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 7, 0),
                        LocalDateTime.of(2025, 10, 1, 11, 0),
                        SleepQuality.GOOD
                )
        );
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult result = function.apply(dayOnly);
        assertEquals(1L, result.getValue());
    }

    @Test
    void testSleeplessNights_WithGapInDays() {
        List<SleepingSession> withGap = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 0),
                        LocalDateTime.of(2025, 10, 4, 7, 0),
                        SleepQuality.GOOD
                )
        );
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult result = function.apply(withGap);
        assertEquals(1L, result.getValue());
    }

    @Test
    void testSleeplessNights_EmptyList() {
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult result = function.apply(Collections.emptyList());
        assertEquals(0L, result.getValue());
    }

    @Test
    void testChronotype_DefaultToPigeonOnTie() {
        ChronotypeClassifierFunction function = new ChronotypeClassifierFunction();
        SleepAnalysisResult result = function.apply(sampleSessions);
        assertEquals(Chronotype.PIGEON, result.getValue());
    }
}