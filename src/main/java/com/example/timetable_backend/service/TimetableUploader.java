package com.example.timetable_backend.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class TimetableUploader {

    public void uploadAllTimetables() {
        uploadDayTimetable("monday", List.of(
                createEntry("Computer Networks", "SD", "11:30 a.m. – 1:30 p.m.", "Sem - 4"),
                createEntry("Software Engineering", "SS", "1:50 p.m. – 2:50 p.m.", "Sem - 4"),
                createEntry("Operating Systems Lab", "PGR, ND", "2:50 p.m. – 4:50 p.m.", "Sem - 4")
        ));

        uploadDayTimetable("tuesday", List.of(
                createEntry("Introduction to DBMS", "SS", "12:30 p.m. – 2:50 p.m.", "Sem - 4"),
                createEntry("Object Oriented Programming lab Using Java", "AT, RG", "2:50 p.m. – 4:50 p.m.", "Sem - 4")
        ));

        uploadDayTimetable("wednesday", List.of(
                createEntry("Operating Systems", "PGR", "11:30 a.m. – 1:30 p.m.", "Sem - 4"),
                createEntry("Computer Networks", "SD", "1:50 p.m. – 2:50 p.m.", "Sem - 4"),
                createEntry("Introduction to DBMS Lab", "SS, RG", "2:50 p.m. – 4:50 p.m.", "Sem - 4")
        ));

        uploadDayTimetable("thursday", List.of(
                createEntry("Object Oriented Programming Using Java", "AT", "11:30 a.m. – 12:30 p.m.", "Sem - 4"),
                createEntry("Introduction To DBMS", "SS", "1:50 p.m. – 2:50 p.m.", "Sem - 4"),
                createEntry("Computer Networks Lab", "SD, ND", "2:50 p.m. – 4:50 p.m.", "Sem - 4")
        ));

        uploadDayTimetable("friday", List.of(
                createEntry("Minor Project", "AT, PGR, SD, SS", "10:30 a.m. – 12:30 p.m.", "Sem - 4"),
                createEntry("Operating Systems", "PGR", "12:30 p.m. – 1:30 p.m.", "Sem - 4"),
                createEntry("Object Oriented Programming", "AT", "1:50 p.m. – 2:50 p.m.", "Sem - 4"),
                createEntry("Software Engineering", "SS", "2:50 p.m. – 4:50 p.m.", "Sem - 4")
        ));

        uploadDayTimetable("saturday", List.of(
                createEntry("Minor Project", "AT, PGR, SD, SS", "11:30 a.m. – 1:30 p.m.", "Sem - 4")
        ));
    }

    private void uploadDayTimetable(String day, List<Map<String, Object>> entries) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference slotsRef = db.collection("timetables").document(day).collection("slots");

        try {
            // Fetch existing timetable for the day
            ApiFuture<QuerySnapshot> future = slotsRef.get();
            List<QueryDocumentSnapshot> existing = future.get().getDocuments();

            if (!existing.isEmpty()) {
                System.out.println("Timetable for " + day + " already exists. Skipping upload.");
                return;
            }

            // Fetch all valid teacher usernames
            Set<String> validTeachers = fetchAllTeacherUsernames(db);

            for (Map<String, Object> entry : entries) {
                String teachersRaw = entry.get("teachers").toString();
                String[] teacherList = teachersRaw.split(",");

                boolean allValid = true;
                for (String teacher : teacherList) {
                    String trimmed = teacher.trim();
                    if (!validTeachers.contains(trimmed)) {
                        System.out.println("Invalid teacher '" + trimmed + "' in subject: " + entry.get("subject"));
                        allValid = false;
                    }
                }

                if (allValid) {
                    slotsRef.add(entry);
                } else {
                    System.out.println("Skipping entry: " + entry.get("subject") + " due to invalid teacher(s).");
                }
            }

            System.out.println("Finished uploading timetable for " + day);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private Set<String> fetchAllTeacherUsernames(Firestore db) throws ExecutionException, InterruptedException {
        Set<String> usernames = new HashSet<>();
        CollectionReference teachersRef = db.collection("teachers");
        ApiFuture<QuerySnapshot> query = teachersRef.get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        for (QueryDocumentSnapshot doc : documents) {
            String username = doc.getString("username");
            if (username != null && !username.isBlank()) {
                usernames.add(username.trim());
            }
        }
        return usernames;
    }

    private Map<String, Object> createEntry(String subject, String teachers, String time, String semester) {
        Map<String, Object> map = new HashMap<>();
        map.put("subject", subject);
        map.put("teachers", teachers);
        map.put("time", time);
        map.put("semester", semester);
        return map;
    }
}
