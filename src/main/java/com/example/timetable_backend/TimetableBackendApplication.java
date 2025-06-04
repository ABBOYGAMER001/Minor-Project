package com.example.timetable_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.timetable_backend.service.TimetableUploader;

@SpringBootApplication
public class TimetableBackendApplication implements CommandLineRunner {

    @Autowired
    private TimetableUploader timetableUploader;

    public static void main(String[] args) {
        SpringApplication.run(TimetableBackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        timetableUploader.uploadAllTimetables();  // This uploads all timetables to Firestore
    }
}
