package com.cool.modules.kiosk.service;

import com.cool.core.base.BaseService;
import com.cool.modules.kiosk.entity.KioskMachineEntity;

/**
 * 打印设备
 */
public interface KioskMachineService extends BaseService<KioskMachineEntity> {
    /**
     * 激活
     * @param key
     * @return
     */
    KioskMachineEntity activate(String key);
}
