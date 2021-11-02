package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    public static Connection getConnection(Properties prop) throws Exception {
        Class.forName(prop.getProperty("rabbit.driver"));
        return DriverManager.getConnection(
                prop.getProperty("rabbit.url"),
                prop.getProperty("rabbit.login"),
                prop.getProperty("rabbit.password"));
    }

    public static int readerIntervalProperties() {
        return Integer.parseInt(readProperties("rabbit.properties")
                .getProperty("rabbit.interval"));
    }

    public static Properties readProperties(String name) {
        Properties properties = new Properties();
        try {
            InputStream in = AlertRabbit.class.getClassLoader()
                    .getResourceAsStream(name);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection(readProperties("rabbit.properties"))) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(readerIntervalProperties())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail()
                    .getJobDataMap().get("connection");
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "insert into rabbit(created_date) values (?)")) {
                statement.setDate(1, Date.valueOf(LocalDate.now()));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}