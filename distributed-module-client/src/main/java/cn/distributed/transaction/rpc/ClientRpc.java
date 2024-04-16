package cn.distributed.transaction.rpc;

import cn.distributed.transaction.api.TXApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@Component
@EnableFeignClients(clients = {TXApi.class})
public class ClientRpc {
}
