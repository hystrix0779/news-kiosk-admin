package com.cool.modules.kiosk.entity;

import java.time.LocalDateTime;
import org.dromara.autotable.annotation.Ignore;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/09/11/15.
 * @annotation :
 */

@Getter
@Setter
@Table(value = "kiosk_machine", comment = "打印设备")
public class KioskMachineEntity extends BaseEntity<KioskMachineEntity> {

    @ColumnDefine(comment = "设备名称")
    private String name;

    @ColumnDefine(comment = "设备密钥")
    private String key;

    @ColumnDefine(comment = "负责人")
    private String principal;

    @ColumnDefine(comment = "联系电话")
    private String phone;

    @ColumnDefine(comment = "打印机名称")
    private String printerName;

    @ColumnDefine(comment = "打印机规格")
    private String printerSpec;

    @ColumnDefine(comment = "状态 0:禁用 1：启用", defaultValue = "1")
    private Integer status;

    @ColumnDefine(comment = "激活状态 0:未激活 1：已激活", defaultValue = "0")
    private Integer activateStatus;

    @ColumnDefine(comment = "激活时间")
    private LocalDateTime activateTime;

    @ColumnDefine(comment = "备注")
    private String remark;

    @ColumnDefine(comment = "支付方式 1-微信 2-支付宝")
    private String payType;

    @ColumnDefine(comment = "默认支付方式  1-微信 2-支付宝")
    private Integer defaultPayType;

    @Ignore
    @Column(ignore = true)
    private Boolean online;
}