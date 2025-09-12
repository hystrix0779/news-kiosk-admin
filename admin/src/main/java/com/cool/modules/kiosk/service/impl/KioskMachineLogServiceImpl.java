package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.kiosk.entity.KioskMachineLogEntity;
import com.cool.modules.kiosk.mapper.KioskMachineLogMapper;
import com.cool.modules.kiosk.service.KioskMachineLogService;
import org.springframework.stereotype.Service;

/**
 * 打印设备日志
 */
@Service
public class KioskMachineLogServiceImpl extends BaseServiceImpl<KioskMachineLogMapper, KioskMachineLogEntity> implements KioskMachineLogService {
}