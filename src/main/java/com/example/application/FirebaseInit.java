package com.example.application;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@Service
public class FirebaseInit {

    static Firestore db;

    @PostConstruct
    @Bean
    public static Firestore initialise(){

        try 
        {
            FirestoreOptions firestoreOptions =
            FirestoreOptions.getDefaultInstance().toBuilder()
                .setProjectId("agile-avengers-435801")
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build();
            db = firestoreOptions.getService();
        } catch (IOException e){
        }
        return db;
    }
}
