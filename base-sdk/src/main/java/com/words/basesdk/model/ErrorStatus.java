package com.words.basesdk.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorStatus {
    String code;
    String message;
}
