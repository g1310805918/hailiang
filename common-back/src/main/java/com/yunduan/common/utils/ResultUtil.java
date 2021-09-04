package com.yunduan.common.utils;



/**
 * @author Victor
 */
public class ResultUtil<T> {

    private Result<T> result;

    public ResultUtil() {
        result = new Result<>();
        result.setSuccess(true);
        result.setMsg("success");
        result.setStatus(200);
    }

    public Result<T> setData(T t) {
        this.result.setResult(t);
        this.result.setStatus(200);
        return this.result;
    }

    public Result<T> setSuccessMsg(String msg) {
        this.result.setSuccess(true);
        this.result.setMsg(msg);
        this.result.setStatus(200);
        this.result.setResult(null);
        return this.result;
    }

    public Result<T> setEncryptData(String encrypt, String msg) {
        this.result.setData(encrypt);
        this.result.setStatus(200);
        this.result.setMsg(msg);
        return this.result;
    }

    public Result<T> setUnEncryptData(String data) {
        this.result.setData(data);
        this.result.setStatus(200);
        return this.result;
    }

    public Result<T> setUnEncryptData(String data, String msg) {
        this.result.setData(data);
        this.result.setStatus(200);
        this.result.setMsg(msg);
        return this.result;
    }

//    public Result<T> setEncryptData(String encrypt) {
//        try {
//          //  this.result.setData(encrypt);
//        this.result.setData(AESUtils.encrypt(encrypt));
//            this.result.setStatus(200);
//            this.result.setMsg("操作成功");
//        } catch (Exception e){
//            this.result.setStatus(500);
//            this.result.setMsg("加密失败" + encrypt);
//        }
//        return this.result;
//    }

    public Result<T> setData(T t, String msg) {
        this.result.setResult(t);
        this.result.setStatus(200);
        this.result.setMsg(msg);
        return this.result;
    }

    public Result<T> setErrorMsg(String msg) {
        this.result.setSuccess(false);
        this.result.setMsg(msg);
        this.result.setStatus(500);
        return this.result;
    }

    public Result<T> setErrorMsg(Integer code, String msg) {
        this.result.setSuccess(false);
        this.result.setMsg(msg);
        this.result.setStatus(code);
        return this.result;
    }

    public Result<T> setWrap(Integer code, String msg) {
        this.result.setSuccess(true);
        this.result.setMsg(msg);
        this.result.setStatus(code);
        return this.result;
    }

    public static <T> Result<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    public static <T> Result<T> data(T t, String msg) {
        return new ResultUtil<T>().setData(t, msg);
    }

    public static <T> Result<T> success(String msg) {
        return new ResultUtil<T>().setSuccessMsg(msg);
    }

    public static <T> Result<T> error(String msg) {
        return new ResultUtil<T>().setErrorMsg(msg);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new ResultUtil<T>().setErrorMsg(code, msg);
    }

}
