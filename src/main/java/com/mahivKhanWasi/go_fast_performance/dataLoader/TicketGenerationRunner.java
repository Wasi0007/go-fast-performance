package com.mahivKhanWasi.go_fast_performance.dataLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Order(4)
public class TicketGenerationRunner {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {

            boolean skipTicketGeneration = true;

            if (skipTicketGeneration) {
                return;
            }
//            GenerateTicketType1(restTemplate);

        };
    }

    private void GenerateTicketType1(RestTemplate restTemplate){
        String url = "http://localhost:8080/api/tickets/generate";

        LocalDate startDate = LocalDate.of(2025, 4, 25);
        LocalDate endDate = LocalDate.of(2026, 5, 02);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("date", date.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url, HttpMethod.POST, requestEntity, String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    System.out.println("[" + date + "] Success: " + response.getBody());
                } else {
                    System.err.println("[" + date + "] Failed or empty response. Stopping execution.");
                    break;
                }

            } catch (Exception e) {
                System.err.println("[" + date + "] Exception occurred: " + e.getMessage());
                break;
            }
        }
    }
}
