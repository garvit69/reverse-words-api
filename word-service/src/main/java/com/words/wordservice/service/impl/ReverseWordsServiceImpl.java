package com.words.wordservice.service.impl;

import com.words.schema.base.Error;
import com.words.schema.reverse.ReverseSentenceReq;
import com.words.schema.reverse.ReverseSentenceRes;
import com.words.wordservice.service.ReverseWordsService;
import org.springframework.stereotype.Service;

import static com.words.wordservice.util.WordsServiceConstants.BLANK_WORD_ERROR_CODE;

@Service
public class ReverseWordsServiceImpl implements ReverseWordsService {
    @Override
    public void processRequest(com.words.basesdk.service.ServiceTask serviceTask) {
        ReverseSentenceReq reverseSentenceReq=(ReverseSentenceReq) serviceTask.getRequest();
        String sentence = reverseSentenceReq.getSentence();
        ReverseSentenceRes reverseSentenceRes=(ReverseSentenceRes) serviceTask.getResponse();
        if (sentence == null || sentence.isBlank()){
            ((ReverseSentenceRes) serviceTask.getResponse()).getErrors().add(new Error(BLANK_WORD_ERROR_CODE,"Sentence cannot be all blank character"));
            return;
        }
        reverseSentenceRes.setReversedSentence(reverseSentence(sentence));
    }

    public String reverseSentence(String s) {
        StringBuilder result = new StringBuilder();
        int start = 0;

        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == ' ') {
                for (int j = i - 1; j >= start; j--) {
                    result.append(s.charAt(j));
                }
                if (i < s.length()) {
                    result.append(' ');
                }
                start = i + 1;
            }
        }

        return result.toString();
    }
}
