package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeClassifierFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {

    public static final String DESCRIPTION = "Ваш хронотип пользователя";

    private static final LocalTime OWL_SLEEP_LOWER = LocalTime.of(23, 0);
    private static final LocalTime OWL_SLEEP_UPPER = LocalTime.of(5, 0);
    private static final LocalTime OWL_WAKE_MIN = LocalTime.of(8, 30);

    private static final LocalTime LARK_SLEEP_MAX = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_MAX = LocalTime.of(7, 0);

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

        return new SleepAnalysisResult(DESCRIPTION, userChronotype);
    }

    private Chronotype classifySession(SleepingSession s) {
        LocalTime sleepTime = s.getStartTime().toLocalTime();
        LocalTime wakeTime = s.getEndTime().toLocalTime();

        boolean isOwlSleep = !sleepTime.isBefore(OWL_SLEEP_LOWER) || sleepTime.isBefore(OWL_SLEEP_UPPER);
        boolean isOwlWake = !wakeTime.isBefore(OWL_WAKE_MIN);

        boolean isLarkSleep = sleepTime.isBefore(LARK_SLEEP_MAX);
        boolean isLarkWake = wakeTime.isBefore(LARK_WAKE_MAX);

        if (isOwlSleep && isOwlWake) {
            return Chronotype.OWL;
        } else if (isLarkSleep && isLarkWake) {
            return Chronotype.LARK;
        } else {
            return Chronotype.PIGEON;
        }
    }
}