package com.words.basesdk.aspect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.words.basesdk.model.TrackLog;
import com.words.schema.base.BaseRes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TrackAspect {

    private final ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${track.kafka.topic:words.track.aspect}")
    String trackLogTopicName;

    @Around("@annotation(track)")
    public Object around(ProceedingJoinPoint point, Track track) throws Throwable {

        Instant requestTs = Instant.now();
        String uniqueId = UUID.randomUUID().toString();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        Object requestObj = null;
        String payload = getPayload(request);
        String className = track.requestClassName();
        if (!className.isBlank() && !payload.isBlank()) {
            try {
                Class<?> clazz = Class.forName(className);
                requestObj = objectMapper
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .readValue(payload, clazz);
            } catch (Exception e) {
                log.warn("Failed to deserialize request body into {}: {}", className, e.getMessage());
            }
        }

        Object responseObj = point.proceed();
        Object responseBody = responseObj;
        if (responseObj instanceof ResponseEntity<?> entity) {
            responseBody = entity.getBody();
        }
        String severity="INFO";
        if (!((BaseRes)responseBody).getErrors().isEmpty()){
            severity="ERROR";
        }
        Instant responseTs = Instant.now();
        long executionTimeMs = responseTs.toEpochMilli() - requestTs.toEpochMilli();
        TrackLog trackLog = TrackLog.builder()
                .uniqueId(uniqueId)
                .serviceName(track.serviceName())
                .requestBody(requestObj)
                .responseBody(responseBody)
                .requestTs(requestTs)
                .responseTs(responseTs)
                .executionTimeMs(executionTimeMs)
                .severity(severity)
                .build();
        log.info(trackLog.toString());
        publishTrackLog(trackLog);
        return responseObj;
    }

    private void publishTrackLog(TrackLog logObj) {
        try {
            kafkaTemplate.send(trackLogTopicName, logObj);
        } catch (Exception e) {
            log.error("Failed to publish TrackLog to Kafka", e);
        }
    }
    private String getPayload(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            log.warn("Failed to read request body: {}", e.getMessage());
            return "";
        }
    }
}
