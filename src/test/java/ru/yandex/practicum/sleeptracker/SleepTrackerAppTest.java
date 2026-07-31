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
    void testSleeplessNights_MultipleSessionsInOneNight() {
        // Две сессии в одну и ту же ночь: с 23:00 до 02:00 и с 03:00 до 06:00
        List<SleepingSession> splitNight = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 2, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 3, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                )
        );
        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult result = function.apply(splitNight);
        // Не должно быть бессонных ночей, т.к. это одна и та же ночь
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
    void testChronotype_OwlWithHalfHours() {
        // Засыпает в 23:30, просыпается в 08:30 — должен быть OWL
        List<SleepingSession> owlSessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 8, 30),
                        SleepQuality.GOOD
                )
        );
        ChronotypeClassifierFunction function = new ChronotypeClassifierFunction();
        SleepAnalysisResult result = function.apply(owlSessions);
        assertEquals(Chronotype.OWL, result.getValue());
    }

    @Test
    void testIntersectsNightInterval_LateEveningOnly() {
        // Сон с 22:00 до 23:30 не пересекает интервал 00:00-06:00
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 1, 23, 30),
                SleepQuality.GOOD
        );
        assertFalse(session.intersectsNightInterval());
    }
}