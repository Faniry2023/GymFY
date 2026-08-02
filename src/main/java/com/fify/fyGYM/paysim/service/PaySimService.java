package com.fify.fyGYM.paysim.service;

import com.fify.fyGYM.paysim.model.InfoPaiDevHelper;
import com.fify.fyGYM.paysim.model.ValueQr;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.List;

@Service
public class PaySimService {

    //private static final String BASE_URL = "https://localhost:7110";
    private static final String BASE_URL = "https://paysim.runasp.net";
    private final RestTemplate restTemplate;

    public PaySimService() {
        this.restTemplate = createInsecureRestTemplate();
    }

    //ANCIEN
    /*
    public ValueQr setup(InfoPaiDevHelper model, HttpServletResponse response) {
        String url = BASE_URL + "/developer/info/setup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InfoPaiDevHelper> request = new HttpEntity<>(model, headers);

        ResponseEntity<ValueQr> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, ValueQr.class
        );

        // Transmettre le cookie jwtApiKey au navigateur
        List<String> setCookieHeaders = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookieHeaders != null) {
            for (String setCookie : setCookieHeaders) {
                if (setCookie.startsWith("jwtApiKey")) {
                    String cookieValue = setCookie.split(";")[0].replace("jwtApiKey=", "");
                    Cookie cookie = new Cookie("jwtApiKey", cookieValue);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(false); // Spring Boot est en HTTP local
                    cookie.setPath("/");
                    cookie.setMaxAge(7 * 60);
                    response.addCookie(cookie);
                }
            }
        }

        return responseEntity.getBody();
    }*/

    //VERSION REFUSE DE HTTP PAR C#
    /*
    public ValueQr setup(InfoPaiDevHelper model, HttpServletResponse response) {
        String url = BASE_URL + "/developer/info/setup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InfoPaiDevHelper> request = new HttpEntity<>(model, headers);

        ResponseEntity<ValueQr> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, ValueQr.class
        );

        // ✅ Forward le cookie tel quel via Set-Cookie header
        List<String> setCookieHeaders = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookieHeaders != null) {
            for (String setCookie : setCookieHeaders) {
                if (setCookie.contains("jwtApiKey")) {
                    // On ajoute le header Set-Cookie directement dans la réponse
                    // sans le modifier — le navigateur le reçoit exactement comme le C#
                    response.addHeader(HttpHeaders.SET_COOKIE, setCookie);
                }
            }
        }

        return responseEntity.getBody();
    }*/

    public ValueQr setup(InfoPaiDevHelper model, HttpServletResponse response) {
        String url = BASE_URL + "/developer/info/setup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InfoPaiDevHelper> request = new HttpEntity<>(model, headers);

        ResponseEntity<ValueQr> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, request, ValueQr.class
        );

        List<String> setCookieHeaders = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookieHeaders != null) {
            for (String setCookie : setCookieHeaders) {
                if (setCookie.contains("jwtApiKey")) {

                    // Extraire uniquement la valeur du cookie
                    String cookieValue = null;
                    for (String part : setCookie.split(";")) {
                        part = part.trim();
                        if (part.startsWith("jwtApiKey=")) {
                            cookieValue = part.substring("jwtApiKey=".length());
                            break;
                        }
                    }

                    if (cookieValue != null) {
                        // Reconstruire le cookie adapté à HTTP localhost
                        String newCookie = "jwtApiKey=" + cookieValue
                                + "; Path=/"
                                + "; HttpOnly"
                                + "; Max-Age=420"
                                + "; SameSite=Lax";
                        // ❌ PAS de "Secure" → le navigateur accepte sur http://localhost

                        response.addHeader(HttpHeaders.SET_COOKIE, newCookie);
                    }
                }
            }
        }

        return responseEntity.getBody();
    }


    // ⚠️ DEV UNIQUEMENT — ignore le certificat SSL de localhost
    private RestTemplate createInsecureRestTemplate() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] c, String a) {}
                        public void checkServerTrusted(X509Certificate[] c, String a) {}
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((h, s) -> true);
            return new RestTemplate();
        } catch (Exception e) {
            throw new RuntimeException("Erreur SSL", e);
        }
    }
}
