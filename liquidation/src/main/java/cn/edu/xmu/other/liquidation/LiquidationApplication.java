package cn.edu.xmu.other.liquidation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Auther hongyu lei
 * @Date 2021/12/23
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.privilegegateway","cn.edu.xmu.other.liquidation","cn.edu.xmu.oomall.core"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.other.liquidation.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.other.liquidation.microservice")
public class LiquidationApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiquidationApplication.class, args);
    }
}