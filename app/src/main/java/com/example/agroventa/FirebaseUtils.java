package com.example.agroventa;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseFirestore getFirestoreInstance() {
        return db;
    }
}
