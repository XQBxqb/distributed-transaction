package cn.distributed.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceoApplication.class,args);
    }
}
