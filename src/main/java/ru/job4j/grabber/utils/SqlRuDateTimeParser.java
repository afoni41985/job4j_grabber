package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String parse) {
        List<String> list = Arrays.asList(parse.split("[ ,]+"));
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.parse(list.get(list.size() - 1));
        if (list.contains("сегодня") || list.contains("вчера")) {
            if (!list.contains("сегодня")) {
                localDate = LocalDate.now().minusDays(1);
            }
        } else {
            localDate = LocalDate.of(
                    Integer.parseInt(list.get(2)) + 2000,
                    Integer.parseInt(MONTHS.get(list.get(1))),
                    Integer.parseInt(list.get(0)));
        }
        return localDate.atTime(localTime);
    }

    public static void main(String[] args) {
        SqlRuDateTimeParser s = new SqlRuDateTimeParser();
        System.out.println(s.parse("19 окт 21, 19:22"));
    }
}