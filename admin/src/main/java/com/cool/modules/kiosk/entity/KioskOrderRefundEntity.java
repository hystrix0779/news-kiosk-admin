package com.cool.modules.kiosk.entity;

import com.cool.core.base.BaseEntity;
import com.cool.core.base.TenantEntity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import org.dromara.autotable.annotation.Ignore;
import org.dromara.autotable.annotation.Index;
import org.dromara.autotable.annotation.IndexField;
import org.dromara.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单信息实体类
 */
@Getter
@Setter
@Table(value = "kiosk_order_refund", comment = "订单信息")
@IndexField(field = "createTime", sort = IndexSortTypeEnum.ASC)
public class KioskOrderRefundEntity extends TenantEntity<KioskOrderRefundEntity> {

    @Index
    @ColumnDefine(comment = "订单号")
    private String orderNum;

    @Index
    @ColumnDefine(comment = "退款状态  5-退款中 6-已退款 8-退款失败", defaultValue = "5", notNull = true)
    private Integer refundStatus;

    @Index
    @ColumnDefine(comment = "退款申请时间")
    private Date refundApplyTime;

    @Index
    @ColumnDefine(comment = "退款成功时间")
    private Date refundSuccessTime;

    @ColumnDefine(comment = "备注")
    private String remark;

    @ColumnDefine(comment = "退款原因")
    private String refundReason;

}
