package com.words.basesdk.kafka;

import com.words.basesdk.model.TrackLog;
import com.words.basesdk.service.TrackLogPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "track.kafka.consumer.enabled",
        havingValue = "true"
)
@ConditionalOnBean(TrackLogPersistenceService.class)
public class TrackLogKafkaListener {

    private final TrackLogPersistenceService persistenceService;

    @KafkaListener(
            topics = "${track.kafka.topic}",
            groupId = "${kafka.consumer.group-id}"
    )
    public void consumeTrackLog(TrackLog trackLog) {
        log.info("Consumed TrackLog from topic: {}", trackLog.getUniqueId());
        persistenceService.save(trackLog);
    }
}
