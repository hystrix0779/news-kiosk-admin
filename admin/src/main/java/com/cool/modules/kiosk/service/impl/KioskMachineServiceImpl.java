package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.base.ModifyEnum;
import com.cool.core.util.IDUtils;
import com.cool.modules.kiosk.entity.KioskMachineEntity;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.mapper.KioskMachineMapper;
import com.cool.modules.kiosk.mapper.KioskTemplateMapper;
import com.cool.modules.kiosk.pojo.KioskMessagePojo;
import com.cool.modules.kiosk.service.KioskMachineService;
import com.cool.modules.kiosk.ws.KioskWebSocketHandler;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 打印设备
 */
@Service
public class KioskMachineServiceImpl extends BaseServiceImpl<KioskMachineMapper, KioskMachineEntity>
        implements KioskMachineService {

    @Resource
    private KioskWebSocketHandler kioskWebSocketHandler;
    @Resource
    private KioskTemplateMapper kioskTemplateMapper;

    @Override
    public void modifyBefore(JSONObject requestParams, KioskMachineEntity t, ModifyEnum type) {
        if (type == ModifyEnum.ADD) {
            // 新增设备时候，需要生产16位设备编码
            t.setKey(IDUtils.generateDeviceCode());
        }
    }

    @Override
    public void modifyAfter(JSONObject requestParams, KioskMachineEntity t) {
        if (t.getReset() != null && t.getReset()) {
            KioskMessagePojo po = kioskWebSocketHandler.getKioskMessagePojo(t.getId());
            if (po != null) {
                po.setType(6);
                kioskWebSocketHandler.sendToDevice(po);
            }
        }

    }

    @Override
    public void completion(List<KioskMachineEntity> list) {
        for (KioskMachineEntity p : list) {
            KioskMessagePojo pojo = kioskWebSocketHandler.getKioskMessagePojo(p.getId());
            if (pojo != null) {
                p.setOnline(true);
                // p.setInstructions(pojo.getInstructions());
            } else {
                p.setOnline(false);
            }
        }

    }

    @Override
    public KioskMachineEntity activate(String key) {
        KioskMachineEntity entity = mapper.selectOneByQuery(QueryWrapper.create().eq(KioskMachineEntity::getKey, key));
        if (entity != null) {
            if (entity.getActivateStatus() == 0) {
                entity.setActivateStatus(1);
                entity.setActivateTime(LocalDateTime.now());
                mapper.update(entity);
            }
            getTemplate(entity);
            return entity;
        }
        return null;
    }

    // 查询模板
    public void getTemplate(KioskMachineEntity entity) {
        List<KioskTemplateEntity> list = kioskTemplateMapper
                .selectListByQuery(QueryWrapper.create().eq(KioskTemplateEntity::getMachineId, entity.getId()));
        if (CollUtil.isNotEmpty(list)) {
            entity.setTemplates(list);
        }

    }
}