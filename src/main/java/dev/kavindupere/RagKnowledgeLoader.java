package dev.kavindupere;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RagKnowledgeLoader {

    private final JdbcClient jdbcClient;
    private final VectorStore vectorStore;

    @Value("classpath:/docs/ksp-publications.txt")
    private Resource kspPublicationsResource;

    @PostConstruct
    public void load() {
        Integer count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                .query(Integer.class)
                .single();

        log.info("Vector store count: {}", count);
        if (count == 0) {
            log.info("Loading knowledge base...");
            var textReader = new TextReader(kspPublicationsResource);
            textReader.getCustomMetadata().put("filename", "kspPublicationsResource.txt");

            var textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(textReader.read()));
            log.info("Knowledge base loaded...");
        }
    }

}
