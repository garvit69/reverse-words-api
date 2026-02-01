package com.words.wordservice.controller;

import com.words.basesdk.controller.BaseController;
import com.words.basesdk.service.ServiceTask;
import com.words.schema.base.BaseRes;
import com.words.schema.search.SearchWordReq;
import com.words.schema.search.SearchWordRes;
import com.words.wordservice.service.WordsLookupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/words")
@Slf4j
public class WordsLookupController extends BaseController<SearchWordReq, SearchWordRes> {
    final WordsLookupService wordsLookupService;

    public WordsLookupController(WordsLookupService wordsLookupService) {
        this.wordsLookupService = wordsLookupService;
    }

    @PostMapping("/search")
    public ResponseEntity<? extends BaseRes> processRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        ServiceTask serviceTask=generateServiceTask(httpServletRequest, httpServletResponse);
        try{
            if (hasErrors(serviceTask)){
                return ResponseEntity.status(getHttpStatusCode(serviceTask)).body((BaseRes) serviceTask.getResponse());
            }
            wordsLookupService.processRequest(serviceTask);
            if (hasErrors(serviceTask)){
                return ResponseEntity.status(getHttpStatusCode(serviceTask)).body((BaseRes) serviceTask.getResponse());
            }
            return ResponseEntity.status(getHttpStatusCode(serviceTask)).body((BaseRes) serviceTask.getResponse());

        }catch (Exception e){
            log.error("Internal Exception occurred while processing search words request", e);
            return ResponseEntity.status(500).body((BaseRes) serviceTask.getResponse());
        }
    }

    @Override
    protected String getValidationSchema() {
        return "schema/SearchWordReq.json";
    }
}
