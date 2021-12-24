package cn.edu.xmu.other.liquidation.microservice;

import cn.edu.xmu.other.liquidation.microservice.vo.SimplePaymentRetVo;
import cn.edu.xmu.other.liquidation.microservice.vo.SimpleRefundRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/24
 */
@FeignClient(value = "pay-service")
public interface PayService {
    @GetMapping("/internal/shops/{shopId}/payment")
    InternalReturnObject getPaymentByStateAndTime(@PathVariable Long shopId,
                                                                      @RequestParam(required = false) Byte state,
                                                                      @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")ZonedDateTime beginTime,
                                                                      @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")ZonedDateTime endTime,
                                                                      @RequestParam(defaultValue = "1") Integer page,
                                                                      @RequestParam(defaultValue = "10") Integer pageSize);

    @GetMapping("/internal/shops/{shopId}/refund")
    InternalReturnObject getRefundByStateAndTime(@PathVariable Long shopId,
                                                                    @RequestParam(required = false) Byte state,
                                                                    @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")ZonedDateTime beginTime,
                                                                    @RequestParam(value = "beginTime",required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX")ZonedDateTime endTime,
                                                                    @RequestParam(defaultValue = "1") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize);


}
