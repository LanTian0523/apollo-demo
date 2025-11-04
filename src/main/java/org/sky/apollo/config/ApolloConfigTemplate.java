package org.sky.apollo.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.sky.apollo.ApolloConstants;
import org.springframework.beans.factory.InitializingBean;


@Slf4j
public abstract class ApolloConfigTemplate implements InitializingBean {

    protected String getNamespace() {
        return ApolloConstants.DEFAULT_NAMESPACE;
    }

    protected abstract String getConfigKey();

    /**
     * 初始化配置
     */
    @Override
    public void afterPropertiesSet() {
        try {
            String configValue = getConfigValue();
            if (StringUtils.isNotBlank(configValue)) {
                log.info("Apollo配置初始化成功，key:{}", getConfigKey());
            } else {
                log.warn("Apollo配置值为空，key:{}", getConfigKey());
            }
        } catch (Throwable t) {
            log.error("Apollo配置初始化失败，key:{}", getConfigKey(), t);
            throw new RuntimeException(t);
        }

        // 注册配置变更监听
        registerConfigChangeListener();
    }

    private void registerConfigChangeListener() {
        Config config = ConfigService.getConfig(getNamespace());
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent configChangeEvent) {
                if (configChangeEvent.isChanged(getConfigKey())) {
                    try {
                        String newValue = configChangeEvent.getChange(getConfigKey()).getNewValue();
                        parseConfig(newValue);
                        log.info("Apollo配置变更，key:{}", getConfigKey());
                    } catch (Throwable t) {
                        log.error("Apollo配置变更失败，key:{}", getConfigKey(), t);
                    }
                }
            }
        });

    }

    /**
     * 解析配置
     * @param configValue 配置值
     */
    protected abstract void parseConfig(String configValue);

    protected String getConfigValue() {
        Config config = ConfigService.getConfig(getNamespace());
        return config.getProperty(getConfigKey(), null);
    }
}
