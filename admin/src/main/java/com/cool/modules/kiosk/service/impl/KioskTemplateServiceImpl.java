package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.mapper.KioskTemplateMapper;
import com.cool.modules.kiosk.service.KioskTemplateService;
import org.springframework.stereotype.Service;

/**
 * 打印模板
 */
@Service
public class KioskTemplateServiceImpl extends BaseServiceImpl<KioskTemplateMapper, KioskTemplateEntity> implements KioskTemplateService {
}