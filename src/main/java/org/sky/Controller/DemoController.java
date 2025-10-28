package org.sky.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Value("${demo.message:Default Message}")
    private String message;

    @GetMapping("/config")
    public String getMessage() {


        return "Apollo配置值：" + message;
    }
}
