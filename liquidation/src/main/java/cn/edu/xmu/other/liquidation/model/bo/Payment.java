package cn.edu.xmu.other.liquidation.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Payment {
    public enum State {
        NOT_PAY((byte)0, "待支付"),
        PAY((byte)1, "已支付"),
        RECO((byte)2, "已对账"),
        LIQUI((byte)3, "已清算"),
        CANCEL((byte)4, "已取消"),
        FAIL((byte)5, "失败");

        private static final Map<Byte, Payment.State> stateMap;

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

        public static Payment.State getTypeByCode(Integer code) { return stateMap.get(code); }

        public Byte getCode() { return code; }

        public String getDescription() { return description; }
    }

    public enum Type {
        ORDER((byte)0, "订单"),
        BOND((byte)1, "保证金");


        private static final Map<Byte, Payment.Type> typeMap;

        static {
            typeMap = new HashMap();
            Arrays.stream(Type.values()).forEach(enumitem -> typeMap.put(enumitem.code, enumitem));
        }

        private byte code;
        private String description;

        Type(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Payment.Type getTypeByCode(Integer code) { return typeMap.get(code); }

        public Byte getCode() { return code; }

        public String getDescription() { return description; }
    }

    private Long id;
    private String tradeSn;
    private Long patternId;
    private String documentId;
    private Byte documentType;
    private String descr;
    private Long amount;
    private Long actualAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime payTime;
    private Byte state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime endTime;
}
