package cn.edu.xmu.other.customer.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther hongyu lei
 * @Date 2021/12/4
 */
@Data
@NoArgsConstructor
public class Customer {
    public enum State {
        BACK((byte)0, "后台"),
        NORM((byte)4, "正常"),
        FORBID((byte)6, "封禁");

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

    public enum Deleted {
        NOT_DELETED((byte)0, "未删除"),
        DELETED((byte)1, "已删除");

        private static final Map<Byte, Deleted> stateMap;

        static {
            stateMap = new HashMap();
            Arrays.stream(Deleted.values()).forEach(enumitem -> stateMap.put(enumitem.code, enumitem));
        }

        private byte code;
        private String description;

        Deleted(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Deleted getTypeByCode(Integer code) { return stateMap.get(code); }

        public Byte getCode() { return code; }

        public String getDescription() { return description; }
    }

    private Long id;

    private String userName;

    private String password;


    private String name;

    private Long point;

    private Byte state;

    private String email;

    private String mobile;

    private Deleted deleted;

    private Long modifierId;

    private String modifierName;

    private Long creatorId;

    private String creatorName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

}
