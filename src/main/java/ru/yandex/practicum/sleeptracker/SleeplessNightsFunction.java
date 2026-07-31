package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    public static final String DESCRIPTION = "Количество бессонных ночей";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult(DESCRIPTION, 0L);
        }

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(SleepingSession::intersectsNightInterval)
                .map(SleepingSession::getNightDate)
                .collect(Collectors.toSet());

        LocalDate firstNight = sessions.stream()
                .map(SleepingSession::getNightDate)
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        LocalDate lastNight = sessions.stream()
                .map(SleepingSession::getNightDate)
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;
        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult(DESCRIPTION, Math.max(0L, sleeplessNights));
    }
}