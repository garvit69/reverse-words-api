package com.words.wordservice.service.impl;

import com.words.basesdk.model.TrackLogDocument;
import com.words.basesdk.service.ServiceTask;
import com.words.schema.search.SearchWordReq;
import com.words.schema.search.SearchWordRes;
import com.words.wordservice.mapper.CommonResponseMapper;
import com.words.wordservice.service.WordsLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.words.wordservice.util.WordsServiceConstants.*;

@Service
public class WordsLookupServiceImpl implements WordsLookupService {

    @Autowired
    MongoTemplate mongoTemplate;

    final CommonResponseMapper commonResponseMapper;

    public WordsLookupServiceImpl(CommonResponseMapper commonResponseMapper) {
        this.commonResponseMapper = commonResponseMapper;
    }

    @Override
    public void processRequest(ServiceTask serviceTask) {
        SearchWordReq searchWordReq=(SearchWordReq) serviceTask.getRequest();
        String word = searchWordReq.getWord();
        Boolean getAllRecords = searchWordReq.getGetAllRecords();
        String severity= null;
        if (!(searchWordReq.getSeverity()==null)){
            severity= searchWordReq.getSeverity().value();
        }

        SearchWordRes searchWordRes=(SearchWordRes) serviceTask.getResponse();
        if ((word == null || word.isBlank()) && (getAllRecords == null || !getAllRecords)){
            ((SearchWordRes) serviceTask.getResponse()).getErrors().add(new com.words.schema.base.Error(INVALID_CRITERIA_ERROR_CODE,"Either word must be provided or getAllRecords must be true"));
            return;
        }
        List<TrackLogDocument> results = searchTrackLogs(Optional.ofNullable(word), Optional.ofNullable(getAllRecords), Optional.ofNullable(severity),serviceTask);
        if (results.isEmpty()){
            ((SearchWordRes) serviceTask.getResponse()).getErrors().add(new com.words.schema.base.Error(EMPTY_RESULT_ERROR_CODE,"No records found matching the criteria"));
            return;
        }
        commonResponseMapper.mapTrackLogToSearchWordRes(results, searchWordRes,word);
    }

    @Override
    public List<TrackLogDocument> searchTrackLogs(Optional<String> word, Optional<Boolean> getAllRecords, Optional<String> severity,ServiceTask serviceTask) {
        Query query = buildSearchCriteria(word, getAllRecords, severity, serviceTask);
        if (!serviceTask.getErrorMap().isEmpty()){
            return List.of();
        }
        return mongoTemplate.find(query, TrackLogDocument.class);
    }

    @Override
    public Query buildSearchCriteria(Optional<String> wordOpt,
                                      Optional<Boolean> getAllRecordsOpt,
                                      Optional<String> severityOpt, ServiceTask serviceTask) {

        Query query = new Query();
        boolean getAll = getAllRecordsOpt.orElse(false);

        if (wordOpt.isEmpty() && severityOpt.isEmpty()&&getAll) {
            return new Query();
        }

        if (wordOpt.isPresent() && !wordOpt.get().isBlank()) {
            TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(wordOpt.get());
            query.addCriteria(textCriteria);
        } else if (!getAll) {
            serviceTask.getErrorMap().put(INVALID_CRITERIA_ERROR_CODE,"Either word must be provided or getAllRecords must be true");
            return new Query();
        }

        severityOpt
                .filter(sev -> !sev.isBlank())
                .ifPresent(sev -> query.addCriteria(Criteria.where(SEVERITY).is(sev)));

        query.limit(100);
        return query;
    }
}
