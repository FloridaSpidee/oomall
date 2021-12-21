package cn.edu.xmu.other.liquidation.controller;

import io.swagger.annotations.Api;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther hongyu lei
 * @Date 2021/12/21
 */
@Api(value = "清算服务", tags = "liquidation")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class LiquidationController {

}
