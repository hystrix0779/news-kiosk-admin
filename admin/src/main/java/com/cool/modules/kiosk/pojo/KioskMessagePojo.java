package com.cool.modules.kiosk.pojo;

import java.time.LocalDateTime;
import java.util.List;

import com.cool.modules.kiosk.entity.KioskMachineErrEntity;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author     : Created by niulixiong(hystrix0779@yeah.net) on 2025/07/31/15.
 * @annotation :
 */
@Data
@ToString
public class KioskMessagePojo {
    private Long id;
    private String key;
    private LocalDateTime loginTime;
    private List<Instruction> instructions;
    /**
     * 打印类型 1/null：连接 2：日志 3：指令 4：打印成功 5：打印失败
     */
    private Integer type;
    private KioskMachineErrEntity err;
    private Instruction instruction;
    // 打印成功失败用到
    private String orderNum;
    // 打印失败用到
    private String remark;

    @Getter
    @Setter
    public static class Instruction {
        private String type;
        private String content;
        private JSONObject params;
    }
}
