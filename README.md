📘 Reverse Words Platform

A microservice-based system built with a reusable Base SDK that provides cross-cutting capabilities like tracking, validation, and Kafka integration.

This project demonstrates clean architecture, AOP logging, schema validation, and event-driven observability.

🏗 High-Level Architecture
flowchart LR
    Client -->|HTTP Request| WordService
    WordService -->|@Track Aspect| BaseSDK
    BaseSDK -->|Publish TrackLog| Kafka[(Kafka Topic)]
    Kafka --> PersistService
    PersistService --> DB[(Database)]

📦 Project Modules
Module	Purpose
base-sdk	Shared infrastructure layer (logging, validation, Kafka, models)
word-service	Business APIs (Reverse Sentence, Search Word)
persist-service	Kafka consumer that stores TrackLogs into DB
🧩 Base SDK — Reusable Core

The Base SDK is packaged as a JAR and reused across services.
It removes duplication and centralizes infrastructure logic.

Provides

✔ Controller tracking via annotation
✔ Request/response schema validation
✔ Kafka producer setup
✔ Optional Kafka consumer
✔ Standard logging model (TrackLog)
✔ Base controller abstraction

🎯 @Track Annotation

Controllers simply add:

@Track
@PostMapping("/reverse")
public ReverseSentenceRes reverse(@RequestBody ReverseSentenceReq req) { ... }


Everything else happens automatically via AOP.

🔍 TrackAspect (AOP Engine)

The SDK includes an aspect that intercepts all @Track methods and captures:

Field	Description
uniqueId	Unique request identifier
serviceName	reverse / search
requestBody	Incoming request object
responseBody	Outgoing response object
requestTs	Request timestamp
responseTs	Response timestamp
executionTimeMs	Processing duration
httpStatus	HTTP status code

This data is packaged into a TrackLog and sent to Kafka.

🚀 Event-Driven Logging

Instead of saving logs synchronously:

Word Service publishes TrackLog to Kafka

Persist Service consumes it

Persist Service stores it in DB

This keeps APIs fast and scalable.

🔄 Request Lifecycle (Sequence Diagram)
sequenceDiagram
    participant C as Client
    participant WS as Word Service
    participant AOP as TrackAspect (SDK)
    participant K as Kafka
    participant PS as Persist Service
    participant DB as Database

    C->>WS: HTTP Request
    WS->>AOP: Method intercepted (@Track)
    AOP->>WS: Proceed with business logic
    WS-->>AOP: Return response
    AOP->>K: Publish TrackLog
    AOP-->>C: HTTP Response

    K->>PS: TrackLog event
    PS->>DB: Save TrackLog

🧾 Schema Validation

All requests are validated against JSON schemas stored in:

resources/schema/


Validation errors are collected and returned in structured responses instead of throwing runtime exceptions.

🧠 Base Controller Abstraction

Controllers extend SDK base logic that provides:

✔ Schema validation
✔ Error aggregation
✔ Standard response flow

Business services focus only on logic — SDK handles infrastructure.

🧱 TrackLog Model

This model represents a full API transaction:

{
  "uniqueId": "uuid",
  "serviceName": "reverse",
  "requestBody": { },
  "responseBody": { },
  "requestTs": "timestamp",
  "responseTs": "timestamp",
  "executionTimeMs": 123,
  "httpStatus": 200
}

💾 TrackLog Database Schema
Example SQL Table
CREATE TABLE track_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    unique_id VARCHAR(100),
    service_name VARCHAR(50),
    request_body TEXT,
    response_body TEXT,
    request_ts TIMESTAMP,
    response_ts TIMESTAMP,
    execution_time_ms BIGINT,
    http_status INT
);

Example Mongo Document
{
  "_id": "ObjectId",
  "uniqueId": "uuid",
  "serviceName": "reverse",
  "requestBody": { },
  "responseBody": { },
  "requestTs": "ISODate",
  "responseTs": "ISODate",
  "executionTimeMs": 123,
  "httpStatus": 200
}

⚙️ Kafka Configuration
Producer (auto-used by SDK)
kafka:
  producer:
    bootstrap-servers: localhost:9092

Consumer (enabled only in persist-service)
track:
  kafka:
    consumer:
      enabled: true

🧠 Why This Design Works
Problem	Solution
Duplicate logging code	AOP with @Track
Controller clutter	SDK abstraction
Blocking DB logging	Kafka async events
Tight coupling	Shared SDK layer
Hard observability	Standard TrackLog model
Scalability issues	Separate persist-service
📌 Summary

This project showcases:

✔ Clean microservice architecture
✔ Reusable SDK-based infrastructure
✔ Annotation-driven request tracking
✔ Centralized schema validation
✔ Event-driven logging via Kafka

The Base SDK acts as a foundational framework for future services to plug into with minimal effort.
