package cn.edu.xmu.other.customer.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuchen Huang
 * @date 2021-12-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    public enum State {
        COLLECTED((byte)1, "已领取"),
        USED((byte)2, "已使用"),
        NONAVAILABLE((byte)3,"已失效");
        private static final Map<Byte, State> typeMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            typeMap = new HashMap();
            for (State enum1 : values()) {
                typeMap.put(enum1.code, enum1);
            }
        }

        private Byte code;
        private String description;

        State(Byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Byte code) {
            return typeMap.get(code);
        }

        public Byte getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

    }
    Long id;
    String couponSn;
    String name;
    Long customerId;
    Long activityId;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    Byte state;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    Long creatorId;
    String creatorName;
}
