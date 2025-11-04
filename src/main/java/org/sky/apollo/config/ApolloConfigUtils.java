package org.sky.apollo.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.sky.apollo.ApolloConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class ApolloConfigUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getConfigValue(String configKey) {
        return getConfigValueWithNamespace(ApolloConstants.DEFAULT_NAMESPACE, configKey);
    }

    public static String getConfigValueWithNamespace(String namespace, String configKey) {
        try {
            Config config = ConfigService.getConfig(namespace);
            String value = config.getProperty(configKey, null);
            if (StringUtils.isBlank(value)) {
                log.warn("Apollo配置值为空， namespace:{}, configKey:{}", namespace, configKey);
            }
            return value;
        } catch (Exception e) {
            log.error("获取Apollo配置值失败， namespace:{}, configKey:{}", namespace, configKey, e);
            return null;
        }
    }

    public static String getConfigValue(String configKey, String defaultValue) {
        return getConfigValueWithDefault(configKey, defaultValue);
    }

    private static String getConfigValueWithDefault(String configKey, String defaultValue) {
        try {
            Config config = ConfigService.getConfig(ApolloConstants.DEFAULT_NAMESPACE);
            return config.getProperty(configKey, defaultValue);
        } catch (Exception e) {
            log.error("获取Apollo配置值失败， namespace:{}, configKey:{}", ApolloConstants.DEFAULT_NAMESPACE, configKey, e);
            return defaultValue;
        }
    }

    /**
     * 获取Apollo配置值并将其解析为List<String>
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return List<String>结果
     */
    public static List<String> getConfigListValue(String configKey, List<String> defaultValue) {
        try {
            String jsonString = getConfigValue(configKey, null);
            if (StringUtils.isBlank(jsonString)) {
                return defaultValue;
            }
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析Apollo配置List值失败，configKey:{}", configKey, e);
            return defaultValue;
        }
    }
}