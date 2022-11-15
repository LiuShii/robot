package com.ph.robot;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ph
 * @description main
 * @since 2022/11/15 12:13
 */
@EnableSimbot
@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Slf4j
public class RobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotApplication.class, args);
        log.info("机器人启动成功！");
    }
}
