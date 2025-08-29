package com.example.bfhjavawebhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookWorkflow {

  private final RestTemplate restTemplate;
  private final ResourceLoader resourceLoader;
  private final ObjectMapper mapper = new ObjectMapper();

  @Value("${candidate.name}")    private String candidateName;
  @Value("${candidate.regNo}")   private String regNo;
  @Value("${candidate.email}")   private String email;
  @Value("${final.query.location}") private String finalQueryLocation;
  @Value("${generate.webhook.url}") private String generateWebhookUrl;
  @Value("${submission.fallbackUrl}") private String submissionFallbackUrl;
  @Value("${submission.useBearerPrefix:false}") private boolean useBearerPrefix;

  public WebhookWorkflow(RestTemplate restTemplate, ResourceLoader resourceLoader) {
    this.restTemplate = restTemplate;
    this.resourceLoader = resourceLoader;
  }

  public void runOnceOnStartup() {
    // 1) Call generateWebhook on startup
    Map<String, Object> genReq = new HashMap<>();
    genReq.put("name", candidateName);
    genReq.put("regNo", regNo);
    genReq.put("email", email);

    ResponseEntity<Map> genResp = restTemplate.postForEntity(generateWebhookUrl, genReq, Map.class);
    if (!genResp.getStatusCode().is2xxSuccessful() || genResp.getBody() == null) {
      throw new RuntimeException("generateWebhook failed: " + genResp.getStatusCode());
    }

    Map body = genResp.getBody();
    String webhookUrl = (String) body.getOrDefault("webhook", body.getOrDefault("webhookUrl", submissionFallbackUrl));
    String accessToken = (String) body.getOrDefault("accessToken", body.get("token"));

    if (accessToken == null) {
      throw new RuntimeException("No accessToken in response: " + body);
    }

    // 2) Decide which question you got (odd/even last two digits)
    int lastTwo = parseLastTwoDigits(regNo);
    boolean odd = (lastTwo % 2 != 0);
    System.out.println("Assigned question set: " + (odd ? "ODD (Question 1)" : "EVEN (Question 2)"));

    // 3) Load your final SQL query from file
    String finalQuery = readFinalQuery(finalQueryLocation);
    if (finalQuery.isBlank()) {
      throw new RuntimeException("final_query.sql is empty. Put your SQL in it.");
    }

    // 4) Submit the final SQL
    String authValue = useBearerPrefix ? ("Bearer " + accessToken) : accessToken;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", authValue);

    Map<String, Object> submitBody = new HashMap<>();
    submitBody.put("finalQuery", finalQuery);

    HttpEntity<Map<String, Object>> submitEntity = new HttpEntity<>(submitBody, headers);
    ResponseEntity<String> submitResp = restTemplate.postForEntity(webhookUrl, submitEntity, String.class);

    if (!submitResp.getStatusCode().is2xxSuccessful()) {
      submitResp = restTemplate.postForEntity(submissionFallbackUrl, submitEntity, String.class);
      if (!submitResp.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException("Submission failed: " + submitResp.getStatusCode() + " body=" + submitResp.getBody());
      }
    }

    System.out.println("Submission success. Server says: " + submitResp.getBody());
  }

  private static int parseLastTwoDigits(String regNo) {
    String digits = regNo.replaceAll("\\D+", "");
    if (digits.length() >= 2) {
      String last2 = digits.substring(digits.length() - 2);
      return Integer.parseInt(last2);
    }
    return Integer.parseInt(digits);
  }

  private String readFinalQuery(String location) {
    try {
      Resource resource = resourceLoader.getResource(location);
      byte[] bytes = resource.getInputStream().readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8).trim();
    } catch (Exception e) {
      throw new RuntimeException("Unable to read final SQL from " + location, e);
    }
  }
}
