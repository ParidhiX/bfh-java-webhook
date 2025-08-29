package com.example.bfhjavawebhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BfhJavaWebhookApplication {

  public static void main(String[] args) {
    SpringApplication.run(BfhJavaWebhookApplication.class, args);
  }

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder b) {
    return b.build();
  }

  @Bean
  ApplicationRunner runner(WebhookWorkflow workflow) {
    return args -> workflow.runOnceOnStartup();
  }
}
