package cn.edu.xmu.other;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Auther hongyu lei
 * @Date 2021/12/11
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.privilegegateway","cn.edu.xmu.other.customer"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.other.customer.mapper")
@EnableDiscoveryClient
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}