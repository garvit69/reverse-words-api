package com.words.basesdk.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.words.basesdk.util.EncryptionDecryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@Slf4j
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.mongodb.database}")
    private String databaseName;

    @Value("${spring.mongodb.host}")
    private String mongoHost;

    @Value("${spring.mongodb.username}")
    private String mongoUsername;

    @Value("${spring.mongodb.encryptPassword}")
    private String mongoPassword;

    @Bean
    @Override
    public MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(),databaseName);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        try {

            //String decryptedPassword = EncryptionDecryptionUtil.decrypt(mongoPassword);

            ConnectionString connectionString = new ConnectionString(
                    "mongodb+srv://" + mongoUsername + ":" + mongoPassword + "@" + mongoHost + "/?retryWrites=true&w=majority&appName=Cluster0"
            );

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .retryWrites(true)
                    .build();

            return MongoClients.create(settings);

        } catch (Exception e) {
            throw new RuntimeException("Error creating MongoClient", e);
        }
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }
}
