package com.yunduan.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Victor
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ningbo.ratelimit")
public class HighsetLimitProperties {

    /**
     * 是否开启全局限流
     */
    private Boolean enable = false;

    /**
     * 限制请求个数
     */
    private Integer limit = 100;

    /**
     * 每单位时间内（毫秒）
     */
    private Integer timeout = 1000;
}
