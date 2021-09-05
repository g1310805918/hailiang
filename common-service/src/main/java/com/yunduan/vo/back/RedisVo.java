package com.yunduan.vo.back;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class RedisVo {

    private String key;

    private String value;

}
