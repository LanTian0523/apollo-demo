package org.sky;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.sky.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableApolloConfig
public class ApolloDemoApplication implements CommandLineRunner {

    @Autowired
    private MessageProducer producer;

    public static void main(String[] args) {
        SpringApplication.run(ApolloDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        producer.send("Hello RocketMQ");
    }
}