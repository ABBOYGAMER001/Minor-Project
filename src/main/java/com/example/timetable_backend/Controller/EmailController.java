package com.example.timetable_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.timetable_backend.service.EmailService;
import com.example.timetable_backend.service.FirestoreService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private FirestoreService firestoreService;

    @PostMapping("/sendToStudents")
    public ResponseEntity<Map<String, String>> sendToStudents() {
        String day = firestoreService.getCurrentDay();
        String timetableBody = firestoreService.getFormattedTimetableForDay(day);
        String subject = "Today's Timetable (" + day + ")";

        List<String> studentEmails = firestoreService.getAllStudentEmails();
        Map<String, String> response = new HashMap<>();

        if (studentEmails.isEmpty()) {
            response.put("message", "No student emails found.");
            return ResponseEntity.ok(response);
        }

        for (String email : studentEmails) {
            System.out.println("Sending to student: " + email);
            emailService.sendTimetableEmail(email, subject, timetableBody);
        }

        response.put("message", "Timetable sent to students.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendToTeachers")
    public ResponseEntity<Map<String, String>> sendToTeachers() {
        String day = firestoreService.getCurrentDay();
        String timetableBody = firestoreService.getFormattedTimetableForDay(day);
        String subject = "Today's Timetable (" + day + ")";

        List<String> teacherEmails = firestoreService.getAllTeacherEmails();
        Map<String, String> response = new HashMap<>();

        if (teacherEmails.isEmpty()) {
            response.put("message", "No teacher emails found.");
            return ResponseEntity.ok(response);
        }

        for (String email : teacherEmails) {
            System.out.println("Sending to teacher: " + email);
            emailService.sendTimetableEmail(email, subject, timetableBody);
        }

        response.put("message", "Timetable sent to teachers.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel-class")
    public ResponseEntity<Map<String, String>> cancelClass(@RequestBody Map<String, String> request) {
        String teacherUid = request.get("uid");
        Map<String, String> response = new HashMap<>();

        if (teacherUid == null || teacherUid.isEmpty()) {
            response.put("message", "Teacher UID is required.");
            return ResponseEntity.badRequest().body(response);
        }

        String teacherName = firestoreService.getTeacherNameByUid(teacherUid);
        if (teacherName == null || teacherName.isEmpty()) {
            teacherName = "The teacher";
        }

        String message = "Note: " + teacherName + " Sir will not be taking class today.";
        String subject = "Class Cancellation Notice";

        List<String> studentEmails = firestoreService.getAllStudentEmails();
        if (studentEmails.isEmpty()) {
            System.out.println("No student emails found for cancellation notice.");
            response.put("message", "No student emails to notify.");
            return ResponseEntity.ok(response);
        }

        for (String email : studentEmails) {
            System.out.println("Notifying student: " + email);
            emailService.sendTimetableEmail(email, subject, message);
        }

        response.put("message", "Cancellation email sent to all students.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-email")
    public ResponseEntity<Map<String, String>> testEmail() {
        String testEmail = "kpminorproject@gmail.com";
        System.out.println("Sending test email to: " + testEmail);
        emailService.sendTimetableEmail(testEmail, "Test Email from Spring Boot", "If you see this, email sending works!");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test email sent to " + testEmail);
        return ResponseEntity.ok(response);
    }
}
