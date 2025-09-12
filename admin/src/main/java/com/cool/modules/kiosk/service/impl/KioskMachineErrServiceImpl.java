package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.kiosk.entity.KioskMachineErrEntity;
import com.cool.modules.kiosk.mapper.KioskMachineErrMapper;
import com.cool.modules.kiosk.service.KioskMachineErrService;
import org.springframework.stereotype.Service;

/**
 * 打印设备错误日志
 */
@Service
public class KioskMachineErrServiceImpl extends BaseServiceImpl<KioskMachineErrMapper, KioskMachineErrEntity> implements KioskMachineErrService {
}