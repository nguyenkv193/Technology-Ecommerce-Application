package com.project.techstore.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.behavior.dto.UserInteractionExportDto;
import com.project.techstore.recommendation.service.AiRecommendationClient;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class AiRecommendationClientTest {
    private HttpServer server;
    private ExecutorService executor;
    private String baseUrl;

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        executor = Executors.newSingleThreadExecutor();
        server.setExecutor(executor);
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/api/v1";
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
        executor.shutdownNow();
    }

    @Test
    void sendsOnlyCurrentProductPreferencesAndReadsAiScores() throws Exception {
        var requestBody = new AtomicReference<String>();
        var requestMethod = new AtomicReference<String>();
        server.createContext("/api/v1/recommend/user", exchange -> {
            requestMethod.set(exchange.getRequestMethod());
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = "{\"source\":\"content_based\",\"recommendations\":[{\"product_id\":3,\"score\":0.8,\"reason\":\"Similar\"}]}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        var client = new AiRecommendationClient(RestClient.builder(), baseUrl, 3000);
        var result = client.forUser(List.of(UserInteractionExportDto.builder()
                .userId(7L).productId(1L).score(4.0).build()), 8);
        var body = new ObjectMapper().readTree(requestBody.get());
        assertEquals("POST", requestMethod.get());
        assertEquals(8, body.get("limit").asInt());
        assertEquals(1, body.get("interactions").size());
        assertEquals(1, body.get("interactions").get(0).get("product_id").asInt());
        assertEquals(4.0, body.get("interactions").get(0).get("score").asDouble());
        assertFalse(requestBody.get().contains("userId"));
        assertEquals(3L, result.getFirst().productId());
        assertEquals(0.8, result.getFirst().score());
    }

    @Test
    void rejectsMissingRecommendationList() {
        server.createContext("/api/v1/recommend/similar/1", exchange -> {
            byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        var client = new AiRecommendationClient(RestClient.builder(), baseUrl, 3000);
        assertThrows(IllegalStateException.class, () -> client.similar(1L, 4));
    }

    @Test
    void slowAiIsInterruptedWithinRequestBudget() {
        server.createContext("/api/v1/recommend/similar/1", exchange -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        var client = new AiRecommendationClient(RestClient.builder(), baseUrl, 300);
        assertTimeoutPreemptively(Duration.ofSeconds(2),
                () -> assertThrows(RestClientException.class, () -> client.similar(1L, 4)));
    }
}
