package com.words.basesdk.model;

import com.networknt.schema.ValidationMessage;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationResultWrapper {
    Optional<String> payloadSanitizationResult;
    Optional<Set<ValidationMessage>> schemaValidationResult;
    Optional<Boolean> isCertsValid;
    List<ErrorStatus> errorStatusList;
}
