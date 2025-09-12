package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.modules.kiosk.entity.KioskMachineErrEntity;
import com.cool.modules.kiosk.service.KioskMachineErrService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 打印设备错误日志
 */
@Tag(name = "打印设备错误日志", description = "打印设备错误日志")
@CoolRestController(api = { Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO }, cname = "machineErr")
public class AdminKioskMachineErrController extends BaseController<KioskMachineErrService, KioskMachineErrEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}