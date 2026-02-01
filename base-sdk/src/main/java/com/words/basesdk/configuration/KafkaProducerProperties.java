package com.words.basesdk.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerProperties {
    private String bootstrapServers;
    private String acks;
    private int retries;
    private int batchSize;
    private int lingerMs;
    private int bufferMemory;
    private String securityProtocol;
    private String saslMechanism;
    private String saslJaasConfig;
}

