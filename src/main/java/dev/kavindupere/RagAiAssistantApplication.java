package dev.kavindupere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class RagAiAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagAiAssistantApplication.class, args);
	}

}
