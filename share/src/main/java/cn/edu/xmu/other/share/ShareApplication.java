package cn.edu.xmu.other.share;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Chen Ye
 * @date 2021/12/16
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.other.share","cn.edu.xmu.privilegegateway"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.other.share.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.edu.xmu.other.share.microservice")
public class ShareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareApplication.class, args);
    }
}
