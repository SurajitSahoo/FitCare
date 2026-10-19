package com.fitness.aiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                },
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "maxOutputTokens", 2048
                )
        );

        try {

            String response = webClient.post()
                    .uri(geminiApiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini API response received successfully");

            return extractText(response);

        } catch (WebClientResponseException.TooManyRequests e) {

            log.error(
                    "Gemini API rate/quota limit reached. Status: {}, Response: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new RuntimeException(
                    "Gemini API quota/rate limit exceeded. Please try again later.",
                    e
            );

        } catch (WebClientResponseException e) {

            log.error(
                    "Gemini API error. Status: {}, Response: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new RuntimeException(
                    "Gemini API request failed: " + e.getStatusCode(),
                    e
            );

        } catch (Exception e) {

            log.error("Unexpected error while calling Gemini API", e);

            throw new RuntimeException(
                    "Failed to communicate with Gemini API",
                    e
            );
        }
    }

    private String extractText(String response) {

        if (response == null || response.isBlank()) {
            throw new RuntimeException("Empty response received from Gemini API");
        }

        /*
         * Gemini response normally looks like:
         *
         * {
         *   "candidates": [
         *     {
         *       "content": {
         *         "parts": [
         *           {
         *             "text": "..."
         *           }
         *         ]
         *       }
         *     }
         *   ]
         * }
         */

        try {
            int textIndex = response.indexOf("\"text\"");

            if (textIndex == -1) {
                log.warn("Could not find text field in Gemini response: {}", response);
                return response;
            }

            int colonIndex = response.indexOf(":", textIndex);
            int firstQuote = response.indexOf("\"", colonIndex + 1);

            if (firstQuote == -1) {
                return response;
            }

            StringBuilder text = new StringBuilder();
            boolean escaped = false;

            for (int i = firstQuote + 1; i < response.length(); i++) {

                char c = response.charAt(i);

                if (escaped) {
                    switch (c) {
                        case 'n' -> text.append('\n');
                        case 'r' -> text.append('\r');
                        case 't' -> text.append('\t');
                        case '"' -> text.append('"');
                        case '\\' -> text.append('\\');
                        default -> text.append(c);
                    }

                    escaped = false;

                } else if (c == '\\') {

                    escaped = true;

                } else if (c == '"') {

                    break;

                } else {

                    text.append(c);
                }
            }

            return text.toString();

        } catch (Exception e) {

            log.warn("Could not parse Gemini response. Returning raw response.");

            return response;
        }
    }
}