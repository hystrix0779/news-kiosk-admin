package com.cool.modules.kiosk.entity;

import com.cool.core.base.TenantEntity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

import org.dromara.autotable.annotation.Ignore;
import org.dromara.autotable.annotation.Index;
import org.dromara.autotable.annotation.IndexField;
import org.dromara.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单信息实体类
 */
@Getter
@Setter
@Table(value = "kiosk_order", comment = "订单信息")
@IndexField(field = "createTime", sort = IndexSortTypeEnum.ASC)
public class KioskOrderEntity extends TenantEntity<KioskOrderEntity> {

    @ColumnDefine(comment = "标题")
    private String title;

    @ColumnDefine(comment = "支付方式 0-待支付 1-微信 2-支付宝")
    private Integer payType;

    @ColumnDefine(comment = "支付时间", type = "datetime")
    private LocalDateTime payTime;

    @Index
    @ColumnDefine(comment = "订单号")
    private String orderNum;

    @ColumnDefine(comment = "状态 0-待付款 1-待打印 4-交易完成 5-退款中 6-已退款 7-已关闭", defaultValue = "0", notNull = true)
    private Integer status;

    @ColumnDefine(type = "DECIMAL(10,2)", comment = "价格")
    private BigDecimal price;

    @ColumnDefine(comment = "数量")
    private Integer count;

    @ColumnDefine(comment = "备注")
    private String remark;

    @ColumnDefine(comment = "关闭备注")
    private String closeRemark;

    @Column(ignore = true)
    private String qrCode;

    @ColumnDefine(comment = "模板Id")
    private Long templateId;

    @ColumnDefine(comment = "机器Id")
    private Long machineId;

    @ColumnDefine(comment = "图片地址")
    private String pic;

    @ColumnDefine(comment = "文件路径")
    private String filePath;

    @Ignore
    @Column(ignore = true)
    private String machineName;

    @Ignore
    @Column(ignore = true)
    private String templateName;

}
