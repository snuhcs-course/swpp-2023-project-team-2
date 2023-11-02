package com.goliath.emojihub.springboot.global.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
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
//                .setStorageBucket("emojihub-e2023.appspot.com")
                .build()
            if (FirebaseApp.getApps().isEmpty() ){
                FirebaseApp.initializeApp(options)
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}