package com.stylefeng.guns.rest.modular.vo;

public class ResponseVO<M> {
    //0-成功， 1-失败， 999-系统异常
    private int status;
    private String msg;
    private M data;

    private ResponseVO(){}

    public static<M> ResponseVO success(M m){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(0);
        responseVO.setData(m);
        return responseVO;
    }

    public static ResponseVO serviceFail(String msg){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(1);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public static ResponseVO appFail(String msg){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(999);
        responseVO.setMsg(msg);
        return responseVO;
    }

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
