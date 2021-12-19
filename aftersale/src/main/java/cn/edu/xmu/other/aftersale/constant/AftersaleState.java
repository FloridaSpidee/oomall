package cn.edu.xmu.other.aftersale.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Chen Shuo
 * @date 2021/12/2
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum AftersaleState {
    NEW((byte) 0, "新建"),
    BUYER_IS_TO_DELIVERED((byte) 1, "待买家发货"),
    BUYER_IS_DELIVERED((byte) 2, "买家已发货"),
    TO_BE_REFUNDED((byte) 3, "待退款"),
    SHOP_IS_TO_DELIVERED((byte) 4, "待店家发货"),
    SHOP_IS_DELIVERED((byte) 5, "店家已发货"),
    DONE((byte) 6, "已结束"),
    CANCELED((byte) 7, "已取消"),
    ;

    private Byte code;
    private String name;

    public static AftersaleState valueOf(Byte state) {
        switch (state) {
            case 0:
                return NEW;
            case 1:
                return BUYER_IS_TO_DELIVERED;
            case 2:
                return BUYER_IS_DELIVERED;
            case 3:
                return TO_BE_REFUNDED;
            case 4:
                return SHOP_IS_TO_DELIVERED;
            case 5:
                return SHOP_IS_DELIVERED;
            case 6:
                return DONE;
            case 7:
                return CANCELED;
            default:
                return null;
        }
    }
}



