package com.cool.modules.kiosk.service;

import com.cool.core.base.BaseService;
import com.cool.modules.kiosk.entity.KioskOrderEntity;

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
    KioskOrderEntity create(KioskOrderEntity kioskOrderEntity);
}
