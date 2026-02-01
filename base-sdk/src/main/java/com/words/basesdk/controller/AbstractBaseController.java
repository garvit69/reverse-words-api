package com.words.basesdk.controller;

import com.networknt.schema.ValidationMessage;
import com.words.basesdk.service.ServiceTask;
import com.words.basesdk.model.ErrorStatus;
import com.words.basesdk.model.ValidationResultWrapper;
import com.words.basesdk.validation.CertValidator;
import com.words.basesdk.validation.SchemaValidator;
import com.words.schema.base.Error;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.words.basesdk.util.BaseSDKConstants.INVALID_CERT_ERROR_CODE;
import static com.words.basesdk.util.BaseSDKConstants.SCHEMA_VALIDATION_ERROR_CODE;

@Slf4j
@Component
public abstract class AbstractBaseController {

    @Autowired
    CertValidator certValidator;

    @Autowired
    SchemaValidator schemaValidator;

    protected void authenticateCaller(HttpServletRequest request, ValidationResultWrapper validationResultWrapper){
        boolean isCertValid = certValidator.validateCerts(request);

        if (!isCertValid){
            validationResultWrapper.getErrorStatusList().add(new ErrorStatus(INVALID_CERT_ERROR_CODE, "Certificate Validation Failed"));
        }else{
            validationResultWrapper.setIsCertsValid(Optional.of(true));
        }
    }

    protected void validatePayloadWithSchema(String payload,
                                             ValidationResultWrapper validationResultWrapper) {

        List<ErrorStatus> errorStatusList = new ArrayList<>();

        Optional<Set<ValidationMessage>> validationResponse =
                schemaValidator.validatePayload(payload, getValidationSchema());

        validationResultWrapper.setSchemaValidationResult(validationResponse);

        validationResponse.ifPresent(results -> {
            for (ValidationMessage msg : results) {
                errorStatusList.add(
                        new ErrorStatus(
                                SCHEMA_VALIDATION_ERROR_CODE,
                                "Schema Validation Error: " + msg.getMessage()
                        )
                );
            }
        });

        validationResultWrapper.getErrorStatusList().addAll(errorStatusList);
    }

    protected boolean hasErrors(ServiceTask serviceTask){
        return !serviceTask.getErrorMap().isEmpty();
    }

    protected List<Error> getErrorList(List<ErrorStatus> errorStatusList, Map<String, String> errorMap){
        List<Error> errorList=new ArrayList<>();
        errorStatusList.forEach(errorStatus -> {
            errorList.add(new Error(errorStatus.getCode(), errorStatus.getMessage()));
            errorMap.put(errorStatus.getCode(),errorStatus.getMessage());
        });
        return errorList;
    }

    protected abstract String getValidationSchema();

    protected int getHttpStatusCode(ServiceTask serviceTask){
        if (hasErrors(serviceTask)){
            return 400;
        }
        return 200;
    }

    protected abstract ServiceTask generateServiceTask(HttpServletRequest request, HttpServletResponse response);
}
