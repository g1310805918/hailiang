package com.yunduan.request.back;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonPage {

    private Integer pageNo;

    private Integer pageSize;
}
