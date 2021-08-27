package com.yunduan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("service")
                .globalOperationParameters(getGlobalOperationParameters())
                .enable(true)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yunduan.controller"))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title("Api接口文档")
                        .description("Api接口文档")
                        .version("1.0.0")
                        .contact(new Contact("","",""))
                        .license("The Apache License")
                        .licenseUrl("http://www.baidu.com")
                        .build());
    }



    private List<Parameter> getGlobalOperationParameters() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder token = new ParameterBuilder();
        ParameterBuilder device = new ParameterBuilder();
        ParameterBuilder banben = new ParameterBuilder();
        ParameterBuilder timestamp = new ParameterBuilder();
        ParameterBuilder deviceid = new ParameterBuilder();
        // header query cookie
        token.name("Token").description("token").modelRef(new ModelRef("String")).parameterType("header").required(true);
        device.name("Device").description("设备1.安卓2.苹果3.小程序4.pc").modelRef(new ModelRef("String")).parameterType("header").required(true);
        banben.name("Banben").description("版本号").modelRef(new ModelRef("String")).parameterType("header").required(true);
        timestamp.name("Timestamp").description("时间戳").modelRef(new ModelRef("String")).parameterType("header").required(true);
        deviceid.name("Deviceid").description("设备id").modelRef(new ModelRef("String")).parameterType("header").required(true);
        pars.add(token.build());
        pars.add(device.build());
        pars.add(banben.build());
        pars.add(timestamp.build());
        pars.add(deviceid.build());
        return pars;
    }



}
