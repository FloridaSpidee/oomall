package cn.edu.xmu.other.customer.model.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessfulCartRetVo {
    private Long id;
    private Long quantity;
    private Long price;
}
