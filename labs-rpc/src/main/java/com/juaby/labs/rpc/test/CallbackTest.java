package com.juaby.labs.rpc.test;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.message.RequestMessageBody;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.util.RpcCallbackHandler;
import com.juaby.labs.rpc.util.SerializeTool;
import org.glassfish.grizzly.Connection;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chaos on 16-2-20.
 */
public class CallbackTest {

    public static void main(String[] args) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println("delay 1 seconds, and excute every 3 seconds");
                String method = "(Lcom/juaby/labs/rpc/test/TestBean;Ljava/util/List<Ljava/lang/String;>;Lcom/juaby/labs/rpc/util/RpcCallback;)Lcom/juaby/labs/rpc/test/TestResult;";
                String service = "com/juaby/labs/rpc/test/MessageService";
                String key = service + method;
                Connection connection = RpcCallbackHandler.getCallbackConnection(key);
                if (connection.canWrite()) {
                    ResponseMessageBody<TestResult> responseMessageBody = new ResponseMessageBody<TestResult>();

                    TestResult result = new TestResult();
                    result.setId("2000");
                    result.setName("RESULT_NAME");
                    result.setContent("COME ON BABY");
                    result.setTime(new Date());

                    responseMessageBody.setService(service);
                    responseMessageBody.setMethod(method);
                    responseMessageBody.setReturnClass("Lcom/juaby/labs/rpc/test/TestResult;");
                    responseMessageBody.setBody(result);
                    byte [] body = SerializeTool.serialize(responseMessageBody);
                    RpcMessage sentMessage = new RpcMessage((byte) 1, (byte) 2,
                            (byte) 0x0F, (byte) 0, body);

                    sentMessage.setId(0);
                    sentMessage.setTotalLength(ServiceConfig.HEADER_SIZE + body.length);

                    connection.write(sentMessage);
                }
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

}
