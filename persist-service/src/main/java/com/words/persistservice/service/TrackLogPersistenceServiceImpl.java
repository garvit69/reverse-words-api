package com.words.persistservice.service;

import com.words.basesdk.model.TrackLog;
import com.words.basesdk.model.TrackLogDocument;
import com.words.basesdk.service.TrackLogPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackLogPersistenceServiceImpl implements TrackLogPersistenceService {

    private final MongoTemplate mongoTemplate;

    @Override
    @Async
    public void save(TrackLog trackLog) {
        try {
            TrackLogDocument doc = TrackLogDocument.from(trackLog);
            mongoTemplate.save(doc);
            log.debug("TrackLog saved to MongoDB: {}", trackLog.getUniqueId());
        } catch (Exception e) {
            log.error("Failed to persist TrackLog {}", trackLog.getUniqueId(), e);
            //have a fallback mechanism here to post to a dlq topic[if time permits]
        }
    }
}
