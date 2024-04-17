package cn.distributed.transaction.interceptor;

import cn.distributed.transaction.thread.BTLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class RollbackInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object o) {
        BTLocal.setRollbackStatus();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse rep, Object o, Exception e) {
        BTLocal.remove();
    }
}