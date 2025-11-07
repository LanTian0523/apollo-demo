package org.sky.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Apollo配置工具
 * 可替代Diamond的publishSingle
 * @author lantian
 */
@Component
public class ApolloConfigUtil {
    private final ApolloConfigProperties properties;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public ApolloConfigUtil(ApolloConfigProperties properties) {
        this.properties = properties;
    }

    /**
     * 发布单个配置项
     */
    public void publishSingle(String key, String value, String comment) throws IOException {
        createOrUpdateItem(key, value, comment);
        publishNameSpace("Auto release - " + key);
    }

    /**
     * 批量发布配置项
     */
    public void publishBatch(Map<String, String> configs, String comment) throws IOException {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            createOrUpdateItem(entry.getKey(), entry.getValue(), comment);
        }
        publishNameSpace("Batch release");
    }

    private void publishNameSpace(String releaseTitle) throws IOException {
        String url = String.format("%s/openapi/v1/envs/%s/apps/%s/clusters/%s/namespaces/%s/releases",
                properties.getAdminUrl(),
                properties.getEnv(),
                properties.getAppId(),
                properties.getCluster(),
                properties.getNamespace()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("releaseTitle", releaseTitle);
        body.put("releasedBy", "client");
        body.put("releaseComment", releaseTitle);

        String json = mapper.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", properties.getToken())
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to publish name space " + response.code() + " " + response.message());
            }

        }
    }

    private void createOrUpdateItem(String key, String value, String comment) throws IOException {
        // 检查OpenAPI是否可用
        if (isOpenAPIAvailable()) {
            // 如果OpenAPI可用，使用OpenAPI方式
            executeWithOpenAPI(key, value, comment);
        } else {
            // 如果OpenAPI不可用，提供替代方案的说明
            throw new IOException("Apollo OpenAPI is not available. Please enable OpenAPI on the Apollo server " +
                                "or use alternative methods to manage configurations. " +
                                "You can enable OpenAPI by setting 'apollo.openapi.enabled=true' in the apollo-adminservice configuration.");
        }
    }

    private boolean isOpenAPIAvailable() {
        try {
            String url = String.format("%s/health", properties.getAdminUrl());
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void executeWithOpenAPI(String key, String value, String comment) throws IOException {
        // 构造完整的URL，确保路径正确
        String url = String.format("%s/openapi/v1/envs/%s/apps/%s/clusters/%s/namespaces/%s/items",
                properties.getAdminUrl(),
                properties.getEnv(),
                properties.getAppId(),
                properties.getCluster(),
                properties.getNamespace()
        );

        // 构造请求体
        Map<String, Object> body = new HashMap<>();
        body.put("key", key);
        body.put("value", value);
        body.put("comment", comment);
        body.put("dataChangeCreatedBy", "apollo"); // 使用apollo作为创建者

        String json = mapper.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", properties.getToken())
                .header("Content-Type", "application/json;charset=UTF-8")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 如果返回404，尝试另一种方式
                if (response.code() == 404) {
                    throw new IOException("OpenAPI endpoint not found. Please check if OpenAPI is enabled on the Apollo server. " +
                                        "Response code: " + response.code() + ", message: " + response.message() +
                                        ", URL: " + url);
                } else {
                    throw new IOException("Failed to create/update item. Response code: " + response.code() +
                                        ", message: " + response.message() + ", URL: " + url);
                }
            }
        }
    }
}