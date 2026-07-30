package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(SleepingSession::intersectsNightInterval)
                .map(session -> session.getStartTime().toLocalDate())
                .collect(Collectors.toSet());

        LocalDate firstDate = sessions.stream()
                .map(session -> session.getStartTime().toLocalDate())
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        LocalDate lastDate = sessions.stream()
                .map(session -> session.getStartTime().toLocalDate())
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        long totalNights = ChronoUnit.DAYS.between(firstDate, lastDate) + 1;
        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult("Количество бессонных ночей", Math.max(0L, sleeplessNights));
    }
}