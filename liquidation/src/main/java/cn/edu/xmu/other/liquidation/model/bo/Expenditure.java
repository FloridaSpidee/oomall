package cn.edu.xmu.other.liquidation.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expenditure {
    public enum State {
        NOT_REMIT((byte)0, "未汇出"),
        REMIT((byte)1, "已汇出");

        private static final Map<Byte, State> stateMap;

        static {
            stateMap = new HashMap();
            Arrays.stream(State.values()).forEach(enumitem -> stateMap.put(enumitem.code, enumitem));
        }

        private byte code;
        private String description;

        State(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Integer code) { return stateMap.get(code); }

        public Byte getCode() { return code; }

        public String getDescription() { return description; }
    }
    private Long id;
    private Long liquidId;
    private Long refundId;
    private Long shopId;
    private Long revenueId;
    private Long productId;
    private String productName;
    private Long orderId;
    private Long orderitemId;
    private Integer quantity;
    private Long amount;
    private Long expressFee;//负
    private Long commission;//负
    private Long point;//负
    private Long sharerId;
    private Long shopRevenue;//负
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
