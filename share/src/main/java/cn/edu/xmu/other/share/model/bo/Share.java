package cn.edu.xmu.other.share.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Share {
    public enum State {
        VALID((byte)0, "有效"),
        INVALID((byte)1, "失效");

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
    private Long sharerId;
    private Long shareActId;
    private Long productId;
    private Long onsaleId;
    private Long quantity;
    private Byte state;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
