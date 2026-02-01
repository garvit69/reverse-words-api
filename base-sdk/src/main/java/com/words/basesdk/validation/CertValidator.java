package com.words.basesdk.validation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;

@Slf4j
@Component
public class CertValidator {

    private static final String EXPECTED_ISSUER_DN = "C=IN,O=Organization,OU=OrgUnit,CN=ExpectedIssuer";

    public boolean validateCerts(HttpServletRequest request) {
        try {
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");

            if (certs == null || certs.length == 0) {
                log.error("No client certificate found in the request.");
                return false;
            }

            X509Certificate clientCert = certs[0];
            clientCert.checkValidity();

            String issuerDN = clientCert.getIssuerX500Principal().getName();
            if (!EXPECTED_ISSUER_DN.equals(issuerDN)) {
                log.error("Certificate issuer validation failed. Expected: {}, Found: {}", EXPECTED_ISSUER_DN, issuerDN);
                return false;
            }

            log.info("Certificate is valid and issuer DN matches.");
            return true;
        } catch (Exception e) {
            log.error("Certificate validation failed: {}", e.getMessage());
            return false;
        }
    }
}