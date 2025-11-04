package org.sky.Controller;

import org.sky.apollo.ApolloConstants;
import org.sky.apollo.config.ApolloConfigUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Arrays;

@RestController
public class DemoController {

    @Value("${demo.message:Default Message}")
    private String message;

    @GetMapping("/config")
    public String getMessage() {
        try {
            // 使用新添加的工具方法直接获取List<String>
            List<String> grayProvCodeList = ApolloConfigUtils.getConfigListValue(
                ApolloConstants.AAL_ADDRESS_GRAY_PROV_CODE,
                Arrays.asList("0")
            );

            return "Apollo配置值：" + message + ", 解析结果: " + grayProvCodeList.toString();
        } catch (Exception e) {
            return "解析失败: " + e.getMessage();
        }
    }
}