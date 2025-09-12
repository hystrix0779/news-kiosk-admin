package com.cool.modules.kiosk.entity;

import java.time.LocalDateTime;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/07/31/13.
 * @annotation :
 */
@Getter
@Setter
@Table(value = "kiosk_machine_log", comment = "打印设备日志")
public class KioskMachineLogEntity extends BaseEntity<KioskMachineLogEntity> {

    @ColumnDefine(comment = "设备ID")
    private Long machineId;

    @ColumnDefine(comment = "登入时间")
    private LocalDateTime loginTime;

    @ColumnDefine(comment = "登出时间")
    private LocalDateTime logoutTime;

}
