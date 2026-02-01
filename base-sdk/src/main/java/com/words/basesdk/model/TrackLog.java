package com.words.basesdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackLog {
    private String uniqueId;
    private String serviceName;
    private Object requestBody;
    private Object responseBody;
    private Instant requestTs;
    private Instant responseTs;
    private long executionTimeMs;
    private String severity;
}
