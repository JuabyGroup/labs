package com.juaby.labs.rpc.message;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>׿��</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 11:01.
 */
public class ResponseMessageBody<T> {

    private String returnClass;

    private T body;

    private Exception exception;

    public ResponseMessageBody() {
    }

    public ResponseMessageBody(T body) {
        this.body = body;
    }

    public ResponseMessageBody(Exception exception) {
        this.exception = exception;
    }

    public ResponseMessageBody(String returnClass, T body) {
        this.returnClass = returnClass;
        this.body = body;
    }

    public ResponseMessageBody(T body, Exception exception) {
        this.body = body;
        this.exception = exception;
    }

    public ResponseMessageBody(String returnClass, T body, Exception exception) {
        this.returnClass = returnClass;
        this.body = body;
        this.exception = exception;
    }

    public String getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(String returnClass) {
        this.returnClass = returnClass;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
