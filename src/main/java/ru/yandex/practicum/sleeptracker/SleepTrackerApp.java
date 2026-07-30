package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private final List<Function<List<SleepingSession>, SleepAnalysisResult>> analysisFunctions = List.of(
            new TotalSessionsFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AverageDurationFunction(),
            new BadQualityCountFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeClassifierFunction()
    );

    public static void main(String[] args) {

        System.out.println("=".repeat(50));
        System.out.println("Добро пожаловать в Трекер Сна!");
        System.out.println("Запускаем анализ вашего лога...");
        System.out.println("=".repeat(50));
        System.out.println();

        if (args.length == 0) {
            System.out.println("Ошибка: Укажите путь к файлу лога сна как аргумент командной строки.");
            return;
        }

        String filePath = args[0];
        SleepTrackerApp app = new SleepTrackerApp();

        try {
            List<SleepingSession> sessions = app.parseFile(filePath);

            app.analysisFunctions.stream()
                    .map(func -> func.apply(sessions))
                    .forEach(System.out::println);

            System.out.println();
            System.out.println("=".repeat(50));
            System.out.println("Анализ успешно завершен!");
            System.out.println("=".repeat(50));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public List<SleepingSession> parseFile(String filePath) throws IOException {
        try (java.util.stream.Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::parseLine)
                    .collect(Collectors.toList());
        }
    }

    private SleepingSession parseLine(String line) {
        String[] parts = line.split(";");
        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        return new SleepingSession(start, end, quality);
    }
}