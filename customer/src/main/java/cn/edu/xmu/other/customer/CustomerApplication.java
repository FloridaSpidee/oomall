package cn.edu.xmu.other.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Auther hongyu lei
 * @Date 2021/12/11
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.privilegegateway","cn.edu.xmu.other.customer","cn.edu.xmu.oomall.core"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.other.customer.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.other.customer.microservice")
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}