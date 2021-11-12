package ru.job4j.grabber;

import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.html.Post;
import ru.job4j.html.SqlRuParse;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try (InputStream in = PsqlStore.class.
                getClassLoader().getResourceAsStream("psqlStore.properties")) {
            cfg.load(in);
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("jdbc.url"),
                    cfg.getProperty("jdbc.login"),
                    cfg.getProperty("jdbc.password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = cnn.prepareStatement(
                "insert into post (name, text, link, created) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, post.getTitle());
            st.setString(2, post.getDescription());
            st.setString(3, post.getLink());
            st.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
             st.execute();
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement st = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = st.executeQuery()) {
                while (resultSet.next()) {
                    postList.add(parseRsl(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post post = new Post();
        try (PreparedStatement st =
                     cnn.prepareStatement("select * from post where id = ?")) {
            st.setInt(1, id);
            try (ResultSet resultSet = st.executeQuery()) {
                if (resultSet.next()) {
                    post = parseRsl(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post parseRsl(ResultSet resultSet) throws SQLException {
        return new Post(
        resultSet.getInt("id"),
        resultSet.getString("name"),
        resultSet.getString("text"),
        resultSet.getString("link"),
        resultSet.getTimestamp("created").toLocalDateTime());

    }

    public static void main(String[] args) {
        DateTimeParser data = new SqlRuDateTimeParser();
        SqlRuParse sqlRuParse = new SqlRuParse(data);
        Properties pr = new Properties();
        PsqlStore psqlStore = new PsqlStore(pr);
        List<Post> post = sqlRuParse.list("https://www.sql.ru/forum/job-offers/");
        psqlStore.save(post.get(1));
        psqlStore.save(post.get(2));
        System.out.println(psqlStore.getAll());
        System.out.println(psqlStore.findById(1));
    }
}
