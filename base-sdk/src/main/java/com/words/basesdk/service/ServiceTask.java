package com.words.basesdk.service;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ServiceTask {
    Object request;
    Object response;
    Map<String,String> errorMap;
}
