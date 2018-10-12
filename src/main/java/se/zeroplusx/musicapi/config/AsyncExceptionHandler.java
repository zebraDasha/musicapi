package se.zeroplusx.musicapi.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(AsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("Method name:").append(method.getName()).append("\r\n");
        for (Object param : params) {
            sb.append("Parameter value:").append(param).append("\r\n");
        }
        LOGGER.error(sb.toString(), ex);
    }

}
