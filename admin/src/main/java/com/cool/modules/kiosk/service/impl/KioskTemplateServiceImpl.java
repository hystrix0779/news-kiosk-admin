package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.ModifyEnum;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.mapper.KioskTemplateMapper;
import com.cool.modules.kiosk.service.KioskTemplateService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 打印模板
 */
@Service
public class KioskTemplateServiceImpl extends BaseServiceImpl<KioskTemplateMapper, KioskTemplateEntity>
        implements KioskTemplateService {

    @Override
    public Object page(JSONObject requestParams, Page<KioskTemplateEntity> page, QueryWrapper queryWrapper) {
        if (requestParams.containsKey("machineId")) {
            queryWrapper.eq(KioskTemplateEntity::getMachineId, requestParams.getLong("machineId"));
        } else {
            queryWrapper.isNull(KioskTemplateEntity::getMachineId);
        }
        Page<KioskTemplateEntity> tPage = this.page(page, queryWrapper);
        completion(tPage.getRecords());
        return tPage;
    }

    @Override
    public void modifyBefore(JSONObject requestParams, KioskTemplateEntity t, ModifyEnum type) {
        if (type == ModifyEnum.DELETE) {
            return;
        }
        if (t.getMachineId() != null && t.getIsDefault() == 1) {
            // 把这个机器的所有默认全部去掉
            KioskTemplateEntity update = new KioskTemplateEntity();
            update.setIsDefault(0);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(KioskTemplateEntity::getMachineId, t.getMachineId());
            queryWrapper.eq(KioskTemplateEntity::getIsDefault, 1);
            update(update, queryWrapper);
        }

    }

    @Override
    public void modifyBefore(JSONObject requestParams, KioskTemplateEntity t) {
        if (t == null) {
            return;
        }
        if (t.getMachineId() != null && t.getIsDefault() == 1) {
            // 把这个机器的所有默认全部去掉
            KioskTemplateEntity update = new KioskTemplateEntity();
            update.setIsDefault(0);
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(KioskTemplateEntity::getMachineId, t.getMachineId());
            queryWrapper.eq(KioskTemplateEntity::getIsDefault, 1);
            update(update, queryWrapper);
        }

    }

    @Override
    public Long add(KioskTemplateEntity entity) {
        long count = this.mapper.selectCountByQuery(new QueryWrapper()
                .eq(KioskTemplateEntity::getMachineId, entity.getMachineId())
                .eq(KioskTemplateEntity::getParentId, entity.getParentId()));
        if (count > 0) {
            return -1L;
        }
        mapper.insertSelective(entity);
        return entity.getId();
    }

}