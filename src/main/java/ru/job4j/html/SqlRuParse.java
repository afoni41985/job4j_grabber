package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class SqlRuParse {
    public static List<Post> detailHtml(String url) throws IOException {
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        Document doc = Jsoup.connect(url).get();
        String link = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki"
                + "-qa-i-devops-moskva-do-200t";
        String title = doc.selectFirst(".messageHeader").text();
        String description = doc.select(".msgBody").get(1).text();
        String time = doc.selectFirst(".msgFooter").text();
        LocalDateTime created = timeParser.parse(time.substring(0, time.indexOf("[")));
        return List.of(new Post(title, link, description, created));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(detailHtml("https://www.sql.ru/forum/1325330/lidy-be-fe-"
                + "senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t"));
    }
}

