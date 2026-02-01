package com.words.basesdk.controller;

import com.words.basesdk.service.ServiceTask;
import com.words.basesdk.model.ValidationResultWrapper;
import com.words.schema.base.BaseRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.words.basesdk.util.BaseSDKConstants.INTERNAL_SERVER_ERROR_CODE;

@Slf4j
public abstract class BaseController<R, T extends BaseRes> extends AbstractBaseController {
    protected final Class<R> requestBodyType;
    protected final Class<T> responseType;


    protected BaseController() {
        this.requestBodyType = (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.responseType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Override
    protected ServiceTask generateServiceTask(HttpServletRequest request, HttpServletResponse response) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setErrorMap(new HashMap<>());
        R requestObj = null;
        BaseRes responseObj = null;
        ValidationResultWrapper validationResultWrapper = new ValidationResultWrapper();
        validationResultWrapper.setErrorStatusList(new ArrayList<>());

        try {
            responseObj = responseType.getDeclaredConstructor().newInstance();
            serviceTask.setResponse(responseObj);
            byte[] requestBytes = StreamUtils.copyToByteArray(request.getInputStream());
            String requestBodyString = new String(requestBytes);
            log.debug("Request = {}", requestBodyString);

            authenticateCaller(request, validationResultWrapper);

            if (validationResultWrapper.getErrorStatusList().isEmpty()&&!requestBodyString.isEmpty()) {
                validatePayloadWithSchema(requestBodyString, validationResultWrapper);
            }

            if (!requestBodyString.isEmpty()&&validationResultWrapper.getErrorStatusList().isEmpty()) {
                requestObj = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(requestBodyString, requestBodyType);
                serviceTask.setRequest(requestObj);
            }

        } catch (Exception e) {
            log.error("Error occurred during initial validations", e);
            serviceTask.getErrorMap().put(INTERNAL_SERVER_ERROR_CODE, "Internal Technical Exception");
        } finally {
            Map<String, String> errorMap = new HashMap<>();
            if (!validationResultWrapper.getErrorStatusList().isEmpty() && responseObj != null) {
                responseObj.getErrors().addAll(getErrorList(validationResultWrapper.getErrorStatusList(), errorMap));
            }
            serviceTask.getErrorMap().putAll(errorMap);
        }

        return serviceTask;
    }

}
