package com.example.timetable_backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    public List<String> getAllStudentEmails() {
        return getEmailsFromCollection("students");
    }

    public List<String> getAllTeacherEmails() {
        return getEmailsFromCollection("teachers");
    }

    private List<String> getEmailsFromCollection(String collectionName) {
        List<String> emails = new ArrayList<>();
        try {
            CollectionReference collection = firestore.collection(collectionName);
            ApiFuture<QuerySnapshot> future = collection.get();
            for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                String email = doc.getString("email");
                if (email != null) emails.add(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    public String getCurrentDay() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return today.toString().toLowerCase(); // e.g., "monday"
    }

    public String getFormattedTimetableForDay(String day) {
        StringBuilder formatted = new StringBuilder();
        try {
            CollectionReference timetableRef = firestore.collection("timetables").document(day).collection("slots");
            ApiFuture<QuerySnapshot> future = timetableRef.get();
            List<QueryDocumentSnapshot> slots = future.get().getDocuments();

            formatted.append("Timetable for ").append(day.substring(0, 1).toUpperCase()).append(day.substring(1)).append(":\n\n");

            for (QueryDocumentSnapshot slot : slots) {
                String time = slot.getString("time");
                String subject = slot.getString("subject");
                String teacher = slot.getString("teacher");
                formatted.append("⏰ ").append(time).append(" - ").append(subject).append(" (").append(teacher).append(")\n");
            }

            if (slots.isEmpty()) {
                formatted.append("No classes scheduled.");
            }

        } catch (Exception e) {
            formatted.append("Error loading timetable.");
            e.printStackTrace();
        }
        return formatted.toString();
    }

    // ✅ Improved: Get teacher name by UID safely
    public String getTeacherNameByUid(String uid) {
        try {
            DocumentSnapshot doc = firestore.collection("teachers").document(uid).get().get();
            if (doc.exists() && doc.contains("username")) {
                return doc.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
