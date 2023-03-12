package ru.example.java.spring.hw5.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keystore")
public class KeystoreProperties {
    private String password;
    private String relativeCacertsPath;
}
