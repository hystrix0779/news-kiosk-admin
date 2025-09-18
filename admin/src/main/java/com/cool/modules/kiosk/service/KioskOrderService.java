package com.cool.modules.kiosk.service;

import com.cool.core.base.BaseService;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.pojo.KioskOrderPojo;

/**
 * 订单信息
 */
public interface KioskOrderService extends BaseService<KioskOrderEntity> {

    /**
     * 创建订单
     * 
     * @param kioskOrderEntity 订单信息
     * @return 订单信息
     */
    KioskOrderEntity create(KioskOrderPojo kioskOrderEntity);

    /**
     * 更新订单
     * 
     * @param kioskOrderEntity 订单信息
     * @return 订单信息
     */
    KioskOrderEntity updateOrder(KioskOrderPojo pojo);
}
