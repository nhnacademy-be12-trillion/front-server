package com.nhnacademy.frontserver.common.cartsummary;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class CartSummaryCookieSignedSerializer {

    private final String secretKey;
    private final ObjectMapper objectMapper;
    private static final String ALGORITHM = "HmacSHA256";

    public CartSummaryCookieSignedSerializer(@Value("${app.cookie.secret}") String secretKey, ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
    }

    // 객체 -> "Base64Json.Base64Signature" 문자열 변환
    public <T> String encrypt(T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            String signature = sign(payload);
            return payload + "." + signature;
        } catch (Exception e) {
            log.error("쿠키 서명 생성 실패", e);
            return null;
        }
    }

    // 문자열 -> 객체 변환 (검증 포함)
    public <T> T decrypt(String cookieValue, Class<T> type) {
        if (!StringUtils.hasText(cookieValue) || !cookieValue.contains(".")) {
            return null;
        }

        try {
            String[] parts = cookieValue.split("\\.");
            String payload = parts[0];
            String receivedSignature = parts[1];

            // 서명 검증: 내가 계산한 서명과 일치하는가?
            if (!sign(payload).equals(receivedSignature)) {
                log.warn("쿠키 서명 불일치 (위변조 감지): {}", cookieValue);
                return null;
            }

            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("쿠키 복호화 실패", e);
            return null;
        }
    }

    private String sign(String payload) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM));
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}