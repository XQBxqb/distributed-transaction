package cn.distributed.transaction.interceptor;

import cn.distributed.transaction.consts.BTConsts;
import cn.distributed.transaction.thread.BTLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class FeignApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object o) {
        String xid = req.getHeader(BTConsts.FEIGN_HEADER_XID_NAME);
        String order = req.getHeader(BTConsts.FEIGN_HEADER_ORDER_NAME);
        String bid = req.getHeader(BTConsts.FEIGN_HEADER_BID_NAME);
        BTLocal.setThreadLocal(BTLocal.BTStatus.initStatus(bid,xid,Integer.parseInt(order)));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse rep, Object o, Exception e) {
        BTLocal.remove();
    }
}