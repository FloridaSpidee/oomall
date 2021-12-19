package cn.edu.xmu.other.aftersale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 * @author Chen Shuo
 * @date 2021/12/15
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.other.aftersale", "cn.edu.xmu.privilegegateway"})
@MapperScan("cn.edu.xmu.other.aftersale.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.other.aftersale.microservice")
public class AftersaleApplication {
    public static void main(String[] args) {
        SpringApplication.run(AftersaleApplication.class, args);
    }
}