package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.service.KioskOrderService;
import org.springframework.stereotype.Service;

/**
 * 订单信息
 */
@Service
public class KioskOrderServiceImpl extends BaseServiceImpl<KioskOrderMapper, KioskOrderEntity> implements KioskOrderService {
}