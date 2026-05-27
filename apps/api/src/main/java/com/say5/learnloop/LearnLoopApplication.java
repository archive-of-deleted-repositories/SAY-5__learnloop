package com.say5.learnloop;

import com.say5.learnloop.api.EngineProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EngineProperties.class)
public class LearnLoopApplication {

  public static void main(String[] args) {
    SpringApplication.run(LearnLoopApplication.class, args);
  }
}
