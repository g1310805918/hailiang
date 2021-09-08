package com.yunduan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.ThreadPoolExecutor;


@EnableScheduling
@EnableWebMvc
@EnableTransactionManagement
@MapperScan("com.yunduan.mapper")
@SpringBootApplication
public class WebApplication {


    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }


//    //创建交换机
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange("topic_exchange");
//    }
//
//
//    //创建队列
//    @Bean
//    public Queue queue() {
//        return new Queue("topic");
//    }
//
//
//    //绑定交换机队列以及路由key
//    @Bean
//    public Binding binding() {
//        return BindingBuilder.bind(queue()).to(topicExchange()).with("*.orange.#");
//    }

}
