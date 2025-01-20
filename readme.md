

#Docker compose build cmd

```cd capstoneProject```

 
```docker-compose up --build -d```


#After build create 'error-topic' using "http://localhost:8090/"

#After build to register elasticsearch-sink connectors using Postman.

#POST command: http://localhost:8083/connectors

Execute using with below configuratiob in Body 

{
    "name": "elasticsearch-sink",
    "config": {
      "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
      "tasks.max": "1",  
      "topics": "creditcard-log-topic,access-logs", 
      "connection.url": "http://elasticsearch:9200",  
      "key.ignore": "true",  
      "schema.ignore": "true",  
      "type.name": "kafka-connect",  
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",  
      "value.converter.schemas.enable": "false", 
      "key.converter": "org.apache.kafka.connect.storage.StringConverter", 
      "key.converter.schemas.enable": "false",  
      "topic.index.map": "creditcard-log-topic=credit-card-logs-${timestamp:yyyy.MM.dd}", 
      "elasticsearch.index": "credit-card-logs",  
      "elasticsearch.document.id": "${uuid}", 
      "batch.size": "500", 
      "acks": "all", 
      "errors.tolerance": "all",  
      "errors.log.enable": "true",  
      "errors.deadletterqueue.topic.name": "error-topic"
    }
  }










