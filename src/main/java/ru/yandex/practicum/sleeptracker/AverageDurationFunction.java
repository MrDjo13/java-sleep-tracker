package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class AverageDurationFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    public static final String DESCRIPTION = "Средняя продолжительность сессии (в минутах)";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        double avg = sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0.0);
        return new SleepAnalysisResult(DESCRIPTION, Math.round(avg * 10.0) / 10.0);
    }
}