package com.zcst.upload.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zcst.upload.config.PythonFileAnalysisProperties;
import com.zcst.upload.service.PythonFileAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PythonFileAnalysisServiceImpl implements PythonFileAnalysisService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final PythonFileAnalysisProperties properties;

    @Override
    public Map<String, Object> submit(String ossUrl, String originalFilename, String contentType, long size) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("analysisType", "file");
        payload.put("ossUrl", ossUrl);
        payload.put("fileName", originalFilename);
        payload.put("contentType", contentType);
        payload.put("size", size);
        return postJson(payload);
    }

    @Override
    public Map<String, Object> submitTimetable(String ossUrl, String studentId, String originalFilename, String contentType, long size) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("analysisType", "timetable");
        payload.put("ossUrl", ossUrl);
        payload.put("studentId", studentId);
        payload.put("fileName", originalFilename);
        payload.put("contentType", contentType);
        payload.put("size", size);
        return postJson(payload);
    }

    private Map<String, Object> postJson(Map<String, Object> payload) {
        String baseUrl = properties.getBaseUrl();
        String submitPath = properties.getSubmitPath();
        if (baseUrl == null || baseUrl.isBlank() || submitPath == null || submitPath.isBlank()) {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("submitted", false);
            resp.put("reason", "python.file-analysis.base-url 或 python.file-analysis.submit-path 未配置");
            return resp;
        }

        URI uri = URI.create(baseUrl.replaceAll("/+$", "") + "/" + submitPath.replaceAll("^/+", ""));
        int timeoutSeconds = properties.getTimeoutSeconds() == null ? 10 : properties.getTimeoutSeconds();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(payload)))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("submitted", response.statusCode() >= 200 && response.statusCode() < 300);
            result.put("statusCode", response.statusCode());
            String body = response.body();
            if (body != null && !body.isBlank()) {
                try {
                    JSONObject jsonBody = JSON.parseObject(body);
                    result.put("body", jsonBody);
                } catch (Exception ignore) {
                    result.put("body", body);
                }
            }
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("submitted", false);
            result.put("error", e.getMessage());
            return result;
        }
    }
}
