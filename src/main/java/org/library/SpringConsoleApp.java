package org.library;

import org.library.database.LibraryDatabase;
import org.library.history.HistoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class SpringConsoleApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringConsoleApp.class, args);
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public LibraryDatabase libraryDatabase() {
        return new LibraryDatabase();
    }

    @Bean
    public HistoryService historyService() {
        return new HistoryService("src/main/java/org/Library/History.txt");
    }
}