package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeClassifierFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> counts = sessions.stream()
                .filter(SleepingSession::intersectsNightInterval)
                .map(this::classifySession)
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        long owlCount = counts.getOrDefault(Chronotype.OWL, 0L);
        long larkCount = counts.getOrDefault(Chronotype.LARK, 0L);
        long pigeonCount = counts.getOrDefault(Chronotype.PIGEON, 0L);

        Chronotype userChronotype;
        if (owlCount > larkCount && owlCount > pigeonCount) {
            userChronotype = Chronotype.OWL;
        } else if (larkCount > owlCount && larkCount > pigeonCount) {
            userChronotype = Chronotype.LARK;
        } else {
            userChronotype = Chronotype.PIGEON;
        }

        return new SleepAnalysisResult("Ваш хронотип пользователя", userChronotype);
    }

    private Chronotype classifySession(SleepingSession s) {
        int sleepHour = s.getStartTime().getHour();
        int wakeHour = s.getEndTime().getHour();

        boolean isOwl = (sleepHour >= 23 || sleepHour < 5) && (wakeHour >= 9);
        boolean isLark = (sleepHour < 22) && (wakeHour < 7);

        if (isOwl) {
            return Chronotype.OWL;
        } else if (isLark) {
            return Chronotype.LARK;
        } else {
            return Chronotype.PIGEON;
        }
    }
}