package cn.edu.xmu.other.customer.model.vo;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * @Auther Chen Yixuan
 * @Date 2021/12/13
 */
@Data
public class CartVo {
    @NotNull
    @Min(value = 1)
    private Long productId;

    private Long quantity;

    public CartVo(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
