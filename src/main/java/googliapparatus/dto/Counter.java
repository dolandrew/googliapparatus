package googliapparatus.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Counter {
    private Map<String, Date> sessions = new HashMap<>();
    private List<Date> searches = new ArrayList<>();

    public void search() {
        searches.add(new Date());
    }

    public void session(String uuid) {
        sessions.put(uuid, new Date());
    }

    public void clearOutOldSearches() {
        Date oneDayAgo = new Date(Instant.now().minus(1, DAYS).toEpochMilli());
        List<Date> remaining = searches.stream()
                .filter(date -> date.after(oneDayAgo)).collect(Collectors.toList());
        searches = remaining;
    }

    public void clearOutOldSessions() {
        Date fiveMinutesAgo = new Date(Instant.now().minus(5, MINUTES).toEpochMilli());
        Map<String, Date> remaining = sessions.entrySet().stream()
                .filter(entry -> entry.getValue().after(fiveMinutesAgo)).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
        sessions = remaining;
    }

    public int getSearchesPerMinute() {
        Date oneMinuteAgo = new Date(Instant.now().minus(1, MINUTES).toEpochMilli());
        return (int) searches.stream()
                .filter(date -> date.after(oneMinuteAgo))
                .count();
    }

    public int getSearchesPerHour() {
        Date oneHourAgo = new Date(Instant.now().minus(1, HOURS).toEpochMilli());
        return (int) searches.stream()
                .filter(date -> date.after(oneHourAgo))
                .count();
    }

    public int getSearchesPerDay() {
        Date oneDayAgo = new Date(Instant.now().minus(1, DAYS).toEpochMilli());
        return (int) searches.stream()
                .filter(date -> date.after(oneDayAgo))
                .count();
    }

    public int getActiveUsers() {
        Date fiveMinutesAgo = new Date(Instant.now().minus(5, MINUTES).toEpochMilli());
        return (int) sessions.entrySet().stream()
                .filter(entry -> entry.getValue().after(fiveMinutesAgo))
                .count();
    }
}
