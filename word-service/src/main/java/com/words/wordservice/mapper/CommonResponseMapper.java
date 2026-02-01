package com.words.wordservice.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.words.basesdk.model.TrackLogDocument;
import com.words.schema.reverse.ReverseSentenceReq;
import com.words.schema.reverse.ReverseSentenceRes;
import com.words.schema.search.SearchWordRes;
import com.words.schema.search.WordRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class CommonResponseMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public void mapTrackLogToSearchWordRes(List<TrackLogDocument> results,
                                           SearchWordRes response,
                                           String word) {

        for (TrackLogDocument doc : results) {
            WordRecord record = new WordRecord();

            if (word != null && !word.isBlank()) {
                record.setWord(word);
            }

            ReverseSentenceReq req =
                    objectMapper.convertValue(doc.getRequestBody(), ReverseSentenceReq.class);

            ReverseSentenceRes res =
                    objectMapper.convertValue(doc.getResponseBody(), ReverseSentenceRes.class);

            record.setRequest(req);
            record.setResponse(res);

            response.getRecords().add(record);
        }
    }
}
