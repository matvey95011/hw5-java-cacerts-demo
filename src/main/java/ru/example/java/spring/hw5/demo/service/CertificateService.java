package ru.example.java.spring.hw5.demo.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.example.java.spring.hw5.demo.config.KeystoreProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CertificateService {
    private static final String JAVA_HOME = "java.home";

    private final KeystoreProperties keystoreProperties;

    public CertificateService(MeterRegistry meterRegistry, KeystoreProperties keystoreProperties) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        this.keystoreProperties = keystoreProperties;
        getCerts().forEach(meterRegistry::gauge);
    }

    private Map<String, Long> getCerts() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        Map<String, Long> result = new HashMap<>();
        var keyStore = loadKeyStore();
        var aliases = keyStore.aliases();

        while (aliases.hasMoreElements()) {
            var alias = aliases.nextElement();
            var certificate = (X509Certificate) keyStore.getCertificate(alias);
            result.put(alias, daysLeft(certificate.getNotAfter()));
        }

        result.forEach((key, value) -> log.info("{}: {}", key, value));

        return result;
    }

    private KeyStore loadKeyStore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        var fis = new FileInputStream(System.getProperty(JAVA_HOME) + keystoreProperties.getRelativeCacertsPath());
        var keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(fis, keystoreProperties.getPassword().toCharArray());

        return keystore;
    }

    private long daysLeft(Date date) {
        return ChronoUnit.DAYS.between(Instant.now(), date.toInstant());
    }
}
