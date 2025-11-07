package org.sky.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "apollo")
public class ApolloConfigProperties {

    private String adminUrl;
    private String appId;
    private String env;
    private String cluster;
    private String namespace;
    private String token;

}
