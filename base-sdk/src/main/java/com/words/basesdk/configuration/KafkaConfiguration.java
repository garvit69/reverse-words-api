package com.words.basesdk.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;



import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({
        KafkaProducerProperties.class,
        KafkaConsumerProperties.class
})
public class KafkaConfiguration {

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProducerProperties props, ObjectMapper mapper) {

        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        config.put(ProducerConfig.ACKS_CONFIG, props.getAcks());
        config.put(ProducerConfig.RETRIES_CONFIG, props.getRetries());
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, props.getBatchSize());
        config.put(ProducerConfig.LINGER_MS_CONFIG, props.getLingerMs());
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, props.getBufferMemory());

        config.put("security.protocol", props.getSecurityProtocol());
        config.put("sasl.mechanism", props.getSaslMechanism());
        config.put("sasl.jaas.config", props.getSaslJaasConfig());


        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                new org.springframework.kafka.support.serializer.JsonSerializer<>(mapper)
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf) {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(pf);
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }


    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaConsumerProperties props, ObjectMapper mapper) {

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, props.getGroupId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, props.getAutoOffsetReset());

        config.put("security.protocol", props.getSecurityProtocol());
        config.put("sasl.mechanism", props.getSaslMechanism());
        config.put("sasl.jaas.config", props.getSaslJaasConfig());

        org.springframework.kafka.support.serializer.JsonDeserializer<Object> deserializer =
                new org.springframework.kafka.support.serializer.JsonDeserializer<>(Object.class, mapper);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> cf) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

}
