package com.example.timetable_backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        String firebaseKeyPath = System.getenv("FIREBASE_KEY_PATH");

        if (firebaseKeyPath == null || firebaseKeyPath.isEmpty()) {
            throw new RuntimeException("FIREBASE_KEY_PATH environment variable is not set.");
        }

        try (InputStream serviceAccount = new FileInputStream(firebaseKeyPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    // .setDatabaseUrl("https://<your-project-id>.firebaseio.com") // optional
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase has been initialized.");
            } else {
                System.out.println("⚠️ Firebase is already initialized.");
            }
        }
    }

    @Bean
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}
