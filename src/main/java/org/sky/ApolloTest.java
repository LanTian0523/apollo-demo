package org.sky;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.sky.util.ApolloConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApolloTest implements CommandLineRunner {

    @Autowired
    private ApolloConfigUtil apolloConfigUtil;

    @Override
    public void run(String... args) throws Exception {
        // 先尝试使用Apollo客户端方式
        try {
            Config config = ConfigService.getAppConfig();
            System.out.println("Apollo客户端配置获取成功");
        } catch (Exception e) {
            System.out.println("Apollo客户端配置获取失败: " + e.getMessage());
        }

        // 再尝试使用OpenAPI方式
        try {
            apolloConfigUtil.publishSingle("test.key", "Hello World", "测试");
            System.out.println("OpenAPI发布成功");
        } catch (Exception e) {
            System.out.println("OpenAPI发布失败: " + e.getMessage());
            System.out.println("解决方案建议:");
            System.out.println("1. 请检查Apollo服务器是否启用了OpenAPI功能");
            System.out.println("2. 在apollo-adminservice配置中设置 'apollo.openapi.enabled=true'");
            System.out.println("3. 或者使用Apollo管理界面手动添加配置项");
            System.out.println("4. 或者使用Apollo客户端的@ApolloJsonValue注解来管理复杂配置");

            // 提供替代方案：使用Apollo客户端来读取配置
            try {
                Config config = ConfigService.getAppConfig();
                String value = config.getProperty("test.key", "默认值");
                System.out.println("通过Apollo客户端读取到的test.key值为: " + value);
            } catch (Exception ex) {
                System.out.println("Apollo客户端读取配置也失败了: " + ex.getMessage());
            }
        }

        // 演示批量发布配置的替代方案
        try {
            Map<String, String> configs = new HashMap<>();
            configs.put("batch.key1", "value1");
            configs.put("batch.key2", "value2");
            configs.put("batch.key3", "value3");
            apolloConfigUtil.publishBatch(configs, "批量测试");
            System.out.println("批量配置发布成功");
        } catch (Exception e) {
            System.out.println("批量配置发布失败: " + e.getMessage());
        }
    }
}