package com.cool.modules.kiosk.entity;

import java.math.BigDecimal;
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
@Table(value = "kiosk_template", comment = "打印模板")
public class KioskTemplateEntity extends BaseEntity<KioskTemplateEntity> {

    @ColumnDefine(comment = "模板图片")
    private String pic;

    @ColumnDefine(comment = "模板名称")
    private String name;

    @ColumnDefine(comment = "MD5")
    private String md5;

    @ColumnDefine(comment = "机器ID")
    private Long machineId;

    @ColumnDefine(comment = "价格",type = "decimal(10,2)")
    private BigDecimal price;
    
    @ColumnDefine(comment = "是否默认",defaultValue = "0")
    private Integer isDefault;

    //父级id
    @ColumnDefine(comment = "父级id")
    private Long parentId;
}