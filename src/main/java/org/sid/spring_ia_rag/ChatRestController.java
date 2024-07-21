package org.sid.spring_ia_rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.List;
@RestController
public class ChatRestController { private ChatClient chatClient;
@Value("classpath:/prompts/sytem-message.st")
private Resource systemMessageResource;
    public ChatRestController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    @GetMapping("/chat")
    public String chat(String question){
    String content=chatClient.prompt()
                .user(question)
                .call()
                .content();return content;}
    @GetMapping(value = "/chat2",produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chat2(String question)
    {
        Flux<String>  content=chatClient.prompt()
                .system("")
                .user(question)
                .stream().content();return content;}
    @GetMapping(value = "/sentiment")
    public Sentiment Sentiment(String review){
        return chatClient.prompt()
                .system(systemMessageResource)
                .user(review).call().entity(Sentiment.class);
    }
    @GetMapping(value = "/describe")
    public String depenses() throws IOException {
        byte[]data=new ClassPathResource("Recu_1.png").getContentAsByteArray();
        String userMessageText = """
                Ton role est de faire la reconnaissance optique du texte qui se trouve dans l'image fournie """;

        UserMessage userMessage=new UserMessage(userMessageText, List.of(new Media(MimeTypeUtils.IMAGE_PNG,data)));

        Prompt prompt= new Prompt(userMessage);
        return chatClient.prompt(prompt).call().content();
    }
}
