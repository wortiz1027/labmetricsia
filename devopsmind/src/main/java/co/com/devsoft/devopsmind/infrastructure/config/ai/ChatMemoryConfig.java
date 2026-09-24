package co.com.devsoft.devopsmind.infrastructure.config.ai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class ChatMemoryConfig {

    @Bean
    ChatMemory chatMemory(MongoTemplate mongoTemplate) {

        MongoChatMemoryRepository mongoRepository = MongoChatMemoryRepository.builder()
                .mongoTemplate(mongoTemplate)
                .build();

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(mongoRepository)
                .maxMessages(30)
                .build();
    }

}
