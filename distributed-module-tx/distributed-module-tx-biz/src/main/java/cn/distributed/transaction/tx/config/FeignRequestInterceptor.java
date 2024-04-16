package cn.distributed.transaction.tx.config;

import cn.distributed.transaction.tx.consts.TXConsts;
import cn.distributed.transaction.tx.thread.TXLocal;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header(TXConsts.FEIGN_HEADER_XID_NAME, TXLocal.getXid());
        template.header(TXConsts.FEIGN_HEADER_BID_NAME,TXLocal.getTurnBid());
        template.header(TXConsts.FEIGN_HEADER_ORDER_NAME,String.valueOf(TXLocal.getTurnOrder()));
    }

}
