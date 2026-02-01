package com.words.wordservice.service;

import com.words.basesdk.model.TrackLogDocument;
import com.words.basesdk.service.ServiceTask;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public  interface WordsLookupService {
    void processRequest(ServiceTask serviceTask);
    List<TrackLogDocument> searchTrackLogs(Optional<String> word,
                                           Optional<Boolean> getAllRecords,
                                           Optional<String> severity, ServiceTask serviceTask);
    Query buildSearchCriteria(Optional<String> wordOpt,
                              Optional<Boolean> getAllRecordsOpt,
                              Optional<String> severityOpt,ServiceTask serviceTask);
}
