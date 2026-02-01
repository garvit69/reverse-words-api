package com.words.basesdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "track_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackLogDocument {
    @Id
    private String uniqueId;
    private String serviceName;
    private Object requestBody;
    private Object responseBody;
    private String severity;
    private long executionTimeMs;
    private Object requestTs;
    private Object responseTs;

    public static TrackLogDocument from(TrackLog log) {
        return TrackLogDocument.builder()
                .uniqueId(log.getUniqueId())
                .serviceName(log.getServiceName())
                .requestBody(log.getRequestBody())
                .responseBody(log.getResponseBody())
                .severity(String.valueOf(log.getSeverity()))
                .executionTimeMs(log.getExecutionTimeMs())
                .requestTs(log.getRequestTs())
                .responseTs(log.getResponseTs())
                .build();
    }
}
