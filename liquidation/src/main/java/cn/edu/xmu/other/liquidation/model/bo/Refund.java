package cn.edu.xmu.other.liquidation.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Refund {
    public enum State {
        NOT_REFUND((byte)0, "待退款"),
        REFUND((byte)1, "已退款"),
        RECO((byte)2, "已对账"),
        LIQUI((byte)3, "已清算"),
        CANCEL((byte)4, "已取消"),
        FAIL((byte)5, "失败");

        private static final Map<Byte, Refund.State> stateMap;

        static {
            stateMap = new HashMap();
            Arrays.stream(Refund.State.values()).forEach(enumitem -> stateMap.put(enumitem.code, enumitem));
        }

        private byte code;
        private String description;

        State(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Refund.State getTypeByCode(Integer code) { return stateMap.get(code); }

        public Byte getCode() { return code; }

        public String getDescription() { return description; }
    }

    public enum Type {
        ORDER((byte)0, "订单"),
        BOND((byte)1, "保证金"),
        AFTERSALE((byte)2, "售后");

        private static final Map<Byte, Refund.Type> typeMap;

        static {
            typeMap = new HashMap();
            Arrays.stream(Refund.Type.values()).forEach(enumitem -> typeMap.put(enumitem.code, enumitem));
        }

        private byte code;
        private String description;

        Type(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Refund.Type getTypeByCode(Integer code) { return typeMap.get(code); }

        public Byte getCode() { return code; }

        public String getDescription() { return description; }
    }

    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long amount;
    private String documentId;
    private Byte documentType;
    private Byte state;
}
