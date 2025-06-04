package com.example.timetable_backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.timetable_backend.model.Teacher;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerTeacher(@RequestBody Teacher teacher) {
        Map<String, String> response = new HashMap<>();
        try {
            Firestore db = FirestoreClient.getFirestore();

            // Save to teachers/{random-id}
            String id = UUID.randomUUID().toString();
            db.collection("teachers").document(id).set(teacher);

            response.put("message", "Teacher registered successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Error registering teacher.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
