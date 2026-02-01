package com.words.basesdk.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SchemaValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Set<ValidationMessage>> validatePayload(String payload, String schemaPath) {

        Set<ValidationMessage> errors = new HashSet<>();

        try {
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaPath);

            if (schemaStream == null) {
                errors.add(buildMessage("Schema not found at path: " + schemaPath));
                return Optional.of(errors);
            }

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            JsonSchema schema = factory.getSchema(schemaStream);

            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(payload);
            } catch (Exception e) {
                errors.add(buildMessage("Invalid JSON payload: " + e.getMessage()));
                return Optional.of(errors);
            }

            errors.addAll(schema.validate(jsonNode));

        } catch (Exception e) {
            errors.add(buildMessage("Unexpected validation error: " + e.getMessage()));
        }

        return Optional.of(errors);
    }

    private ValidationMessage buildMessage(String message) {
        return ValidationMessage.builder()
                .code("schema.validation")
                .message(message)
                .build();
    }


}
