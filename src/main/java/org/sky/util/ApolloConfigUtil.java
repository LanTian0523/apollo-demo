package org.sky.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

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

    public void publishSingle(String url, String key, String value, String comment) {
        createOrUpdateItem(key, value, comment);
        publishNameSpace("Auto release - " + key);
    }

    private void publishNameSpace(String s) {
    }

    private void createOrUpdateItem(String key, String value, String comment) {
        String url = String.format("%s/openapi/v1/envs/%s/apps/%s/clusters/%s/namespaces/%s/releases",
                properties.getAdminUrl(),
                properties.getEnv(),
                properties.getAppId(),
                properties.getCluster(),
                properties.getNamespace()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("token", properties.getToken());
        body.put("releaseKey", "Auto release - " + key);
        body.put("items", new String[]{key, value});
    }

}
