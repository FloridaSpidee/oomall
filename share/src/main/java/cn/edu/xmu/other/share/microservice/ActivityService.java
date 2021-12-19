package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Chen Ye
 * @date 2021/12/17
 */
@FeignClient(name = "Activity")
public interface ActivityService {
    @GetMapping("/shops/0/shareactivities/{id}")
    public ReturnObject getShareActivityRetVoById(@PathVariable Long id);
}
