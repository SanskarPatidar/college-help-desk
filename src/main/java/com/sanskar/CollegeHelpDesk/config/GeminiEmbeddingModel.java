package com.sanskar.CollegeHelpDesk.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeminiEmbeddingModel implements EmbeddingModel {

    private final RestTemplate restTemplate;
    private final String url;
    private final String apiKey;
    @Value("${embedding.dimensions:768}")
    private int embeddingDimensionality;

    public GeminiEmbeddingModel(RestTemplate restTemplate, String apiKey) {
        this.restTemplate = restTemplate;
        this.url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent";
        this.apiKey = apiKey;
    }


    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();

        List<String> inputs = request.getInstructions();

        for (int i = 0; i < inputs.size(); i++) {
            float[] vector = embedText(inputs.get(i));

            embeddings.add(new Embedding(vector, i));
        }

        return new EmbeddingResponse(embeddings);
    }

    private float[] embedText(String text) {

        Map<String, Object> body = Map.of(
                "model", "models/gemini-embedding-001",
                "output_dimensionality", embeddingDimensionality,
                "content", Map.of(
                        "parts", List.of(
                                Map.of("text", text)
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map response = restTemplate.postForObject(url, entity, Map.class);

        if (response == null || !response.containsKey("embedding")) {
            throw new RuntimeException("Failed to get embedding from Gemini");
        }

        Map embedding = (Map) response.get("embedding");

        List<Double> values = (List<Double>) embedding.get("values");
        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).floatValue();
        }
        return result;
    }

    @Override
    public float[] embed(Document document) {
        return embedText(document.getText());
    }
}