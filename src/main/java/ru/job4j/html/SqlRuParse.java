package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postList = new ArrayList<>();
        Document document = Jsoup.connect(link).get();
        Elements row = document.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            postList.add(detail(href.attr("href")));
        }
        return postList;
    }

    @Override
    public Post detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        String title = doc.selectFirst(".messageHeader").text();
        String description = doc.select(".msgBody").get(1).text();
        String time = doc.selectFirst(".msgFooter").text();
        LocalDateTime created = dateTimeParser.parse(time.substring(0, time.indexOf("[")));
        return new Post(title, link, description, created);
    }

    public static void main(String[] args) throws Exception {
        DateTimeParser data = new SqlRuDateTimeParser();
        SqlRuParse sqlRuParse = new SqlRuParse(data);
        List<Post> posts = sqlRuParse.list("https://www.sql.ru/forum/job-offers/1");
        System.out.println(posts.get(3).getTitle());
        System.out.println(posts.get(3).getCreated());
        System.out.println(posts.get(3).getDescription());
        System.out.println(posts.get(3).getLink());
    }
}

