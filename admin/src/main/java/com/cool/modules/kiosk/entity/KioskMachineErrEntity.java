package com.cool.modules.kiosk.entity;


import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;

import lombok.Getter;
import lombok.Setter;

/**
 * @author     : Created by niulixiong(hystrix0779@yeah.net) on 2025/07/31/17.
 * @annotation :
 */
@Getter
@Setter
@Table(value = "kiosk_machine_err", comment = "打印设备错误日志")
public class KioskMachineErrEntity extends BaseEntity<KioskMachineErrEntity> {
    
    @ColumnDefine(comment = "设备ID")
    private Long machineId;

    @ColumnDefine(comment = "错误信息",type = "text")
    private String error;

    @ColumnDefine(comment = "错误类型")
    private String type;
}
