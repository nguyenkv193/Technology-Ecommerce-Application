package com.project.techstore.recommendation.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class AiRecommendationClient {
    private final RestClient client;

    public AiRecommendationClient(RestClient.Builder builder,
                                  @Value("${ai.service-url:http://localhost:8001/api/v1}") String serviceUrl,
                                  @Value("${ai.timeout-ms:1500}") int timeoutMs) {
        var timeout = Duration.ofMillis(timeoutMs);
        var httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        // Bound the whole asynchronous request, including DNS resolution.
        factory.setReadTimeout(timeout);
        client = builder.baseUrl(serviceUrl).requestFactory(factory).build();
    }

    public List<AiItem> forUser(List<UserInteractionExportDto> interactions, int limit) {
        var body = new UserRequest(interactions.stream()
                .map(i -> new Interaction(i.getProductId(), i.getScore())).toList(), limit);
        return requireItems(client.post().uri("/recommend/user").body(body).retrieve().body(AiResult.class));
    }

    public List<AiItem> similar(Long productId, int limit) {
        return requireItems(client.get().uri("/recommend/similar/{id}?limit={limit}", productId, limit)
                .retrieve().body(AiResult.class));
    }

    private List<AiItem> requireItems(AiResult result) {
        if (result == null || result.recommendations() == null) {
            throw new IllegalStateException("AI returned an invalid recommendation response");
        }
        return result.recommendations();
    }

    private record Interaction(@JsonProperty("product_id") Long productId, double score) {}
    private record UserRequest(List<Interaction> interactions, int limit) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiItem(@JsonProperty("product_id") Long productId, Double score, String reason) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiResult(List<AiItem> recommendations) {}
}
