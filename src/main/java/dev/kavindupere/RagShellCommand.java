package dev.kavindupere;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.core.io.Resource;
import org.springframework.shell.command.annotation.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Command
public class RagShellCommand {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/ai-assistant.st")
    private Resource promptTemplate;

    public RagShellCommand(ChatClient.Builder chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
    }

    @Command(command = "q")
    public String question(@DefaultValue("What is KSP Publications") String prompt) {

        var promptTemplate = new PromptTemplate(this.promptTemplate);
        Map<String, Object> promptParams = new HashMap<>();
        promptParams.put("input", prompt);
        promptParams.put("documents", String.join("\n", findSimilarity(prompt)));

        return chatClient.prompt(promptTemplate.create(promptParams))
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();
    }

    private List<String> findSimilarity(String prompt) {
        var similarDocuments = vectorStore.similaritySearch(SearchRequest.query(prompt).withTopK(3));
        return similarDocuments.stream().map(Document::getContent).toList();
    }
}
