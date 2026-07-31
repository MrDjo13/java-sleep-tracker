package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SleepingSession {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime startTime, LocalDateTime endTime, SleepQuality quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    public boolean intersectsNightInterval() {
        LocalDateTime nightStart = startTime.toLocalDate().atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);

        LocalDateTime nextNightStart = nightStart.plusDays(1);
        LocalDateTime nextNightEnd = nextNightStart.plusHours(6);

        boolean intersectsFirstNight = startTime.isBefore(nightEnd) && endTime.isAfter(nightStart);
        boolean intersectsNextNight = startTime.isBefore(nextNightEnd) && endTime.isAfter(nextNightStart);

        return intersectsFirstNight || intersectsNextNight;
    }

    public LocalDate getNightDate() {
        LocalDateTime nightEndLimit = startTime.toLocalDate().atStartOfDay().plusHours(6);
        if (startTime.isBefore(nightEndLimit)) {
            return startTime.toLocalDate();
        } else {
            return startTime.toLocalDate().plusDays(1);
        }
    }
}