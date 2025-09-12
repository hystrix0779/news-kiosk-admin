package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.mapper.KioskTemplateMapper;
import com.cool.modules.kiosk.service.KioskTemplateService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.json.JSONObject;

import org.springframework.stereotype.Service;

/**
 * 打印模板
 */
@Service
public class KioskTemplateServiceImpl extends BaseServiceImpl<KioskTemplateMapper, KioskTemplateEntity> implements KioskTemplateService {

    @Override
    public Object page(JSONObject requestParams, Page<KioskTemplateEntity> page, QueryWrapper queryWrapper) {
        if (requestParams.containsKey("machineId")) {
            queryWrapper.eq(KioskTemplateEntity::getMachineId, requestParams.getLong("machineId"));
        }else{
            queryWrapper.isNull(KioskTemplateEntity::getMachineId);
        }
        Page<KioskTemplateEntity> tPage = this.page(page, queryWrapper);
        completion(tPage.getRecords());
        return tPage;
    }
}