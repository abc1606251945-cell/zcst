package com.zcst.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "python.file-analysis")
public class PythonFileAnalysisProperties {

    private String baseUrl;

    private String submitPath;

    private Integer timeoutSeconds = 10;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSubmitPath() {
        return submitPath;
    }

    public void setSubmitPath(String submitPath) {
        this.submitPath = submitPath;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
