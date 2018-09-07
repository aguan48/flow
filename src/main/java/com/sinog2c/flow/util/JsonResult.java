package com.sinog2c.flow.util;

/**
 * @ClassName: JsonResult
 * @Description: TODO(封装请求数据)
 * @author zy
 * @date 2018年7月11日 下午6:07:47
 * 
 */

public class JsonResult<T> {
  private String code;
  private String msg;
  private T data;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }


  @Override
  public String toString() {
    return "code=" + code + " message=" + msg + " data=" + data;
  }

  public static <T> JsonResult<T> fail() {
    JsonResult<T> ret = new JsonResult<T>();
    ret.setCode(JsonReturnCode.FAIL.getCode());
    ret.setMsg(JsonReturnCode.FAIL.getDesc());
    return ret;
  }

  public static <T> JsonResult<T> fail(T data) {
    JsonResult<T> ret = JsonResult.fail();
    ret.setData(data);
    return ret;
  }

  public static <T> JsonResult<T> failMessage(String msg) {
    JsonResult<T> ret = JsonResult.fail();
    ret.setMsg(msg);
    return ret;
  }

  public static <T> JsonResult<T> successMessage(String msg) {
    JsonResult<T> ret = JsonResult.success();
    ret.setMsg(msg);
    return ret;
  }

  public static <T> JsonResult<T> success() {
    JsonResult<T> ret = new JsonResult<T>();
    ret.setCode(JsonReturnCode.SUCCESS.getCode());
    ret.setMsg(JsonReturnCode.SUCCESS.getDesc());
    return ret;
  }

  public static <T> JsonResult<T> success(T data) {
    JsonResult<T> ret = JsonResult.success();
    ret.setData(data);
    return ret;
  }

}
