package com.words.wordservice.service;

import com.words.basesdk.service.ServiceTask;
import org.springframework.stereotype.Service;

@Service
public  interface ReverseWordsService {
    void processRequest(ServiceTask serviceTask);
    String reverseSentence(String sentence);
}
