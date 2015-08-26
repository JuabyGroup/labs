package org.glassfish.grizzly.samples.filterchain;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>×¿±´</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/26 11:01.
 */
public class MessageBody<T> {

    private String returnClass;

    private T body;

    public MessageBody() {
    }

    public MessageBody(String returnClass, T body) {
        this.returnClass = returnClass;
        this.body = body;
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
