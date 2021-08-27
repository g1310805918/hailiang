package com.yunduan.utils;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@ApiModel("统一返回结果")
@Component
public class ResultUtil<T> implements Serializable {

    private static final long serialVersionUID = -21665814217282079L;

    @ApiModelProperty("状态码")
    private Integer status;

    @ApiModelProperty("提示信息")
    private String message;

    @ApiModelProperty("返回数据")
    private T data;


    public ResultUtil(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResultUtil() {
    }

    /**
     * 返回加密后成功数据
     */
    public ResultUtil AesJSONSuccess(String message,T data){
        return new ResultUtil(StatusCodeUtil.SUCCESS_CODE,message, AESUtil.encrypt(JSONObject.toJSONString(data)));
    }


    /**
     * 返回加密后失败数据
     */
    public ResultUtil AesFAILError(String message){
        return new ResultUtil(StatusCodeUtil.SYSTEM_ERROR,message, AESUtil.encrypt("{}"));
    }


    /**
     * 返回成功
     */
    public ResultUtil success(T data){
        return new ResultUtil(StatusCodeUtil.SUCCESS_CODE,"SUCCESS",JSONObject.toJSONString(data));
    }


    /**
     * 返回失败
     */
    public ResultUtil failed(T data){
        return new ResultUtil(StatusCodeUtil.SYSTEM_ERROR,"FAIL",JSONObject.toJSONString(data));
    }


    /**
     * 返回后台成功结果
     */
    public ResultUtil<T> backSuccess(String message, T data){
        return new ResultUtil(StatusCodeUtil.SUCCESS_CODE,message,data);
    }

    /**
     * 返回后台成功结果
     */
    public ResultUtil<String> backFail(String message){
        return new ResultUtil(StatusCodeUtil.SYSTEM_ERROR,message,"{}");
    }


}
