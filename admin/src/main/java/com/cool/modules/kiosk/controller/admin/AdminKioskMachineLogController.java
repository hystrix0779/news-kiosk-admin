package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.modules.kiosk.entity.KioskMachineLogEntity;
import com.cool.modules.kiosk.entity.table.KioskMachineLogEntityTableDef;
import com.cool.modules.kiosk.service.KioskMachineLogService;
import com.mybatisflex.core.query.QueryWrapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 打印设备日志
 */
@Tag(name = "打印设备日志", description = "打印设备日志")
@CoolRestController(api = { Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO }, cname = "machineLog")
public class AdminKioskMachineLogController extends BaseController<KioskMachineLogService, KioskMachineLogEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        KioskMachineLogEntityTableDef tableDef = KioskMachineLogEntityTableDef.KIOSK_MACHINE_LOG_ENTITY;
        setPageOption(createOp()
                .fieldEq(tableDef.MACHINE_ID)
                .queryWrapper(QueryWrapper.create()));
        setListOption(createOp());
    }
}