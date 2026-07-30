package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class MaxDurationFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long max = sessions.stream()
                .filter(s -> s.getQuality() != SleepQuality.BAD)
                .mapToLong(SleepingSession::getDurationMinutes)
                .max()
                .orElse(0L);

        return new SleepAnalysisResult("Максимальная продолжительность сессии (в минутах)", max);
    }
}