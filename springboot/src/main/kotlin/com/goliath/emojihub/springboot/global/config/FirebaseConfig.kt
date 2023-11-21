package com.goliath.emojihub.springboot.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.storage.Storage
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.cloud.StorageClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun init() {
        try {
            val serviceAccount = FileInputStream("springboot/src/main/resources/serviceAccountKey.json")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("emojihub-e2023.appspot.com")
                .build()
            if (FirebaseApp.getApps().isEmpty() ){
                FirebaseApp.initializeApp(options)
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    @Bean
    fun initFirebaseDB(): Firestore {
        return FirestoreClient.getFirestore()
    }

    @Bean
    fun initFirebaseStorage(): Storage {
        return StorageClient.getInstance().bucket().storage
    }
}