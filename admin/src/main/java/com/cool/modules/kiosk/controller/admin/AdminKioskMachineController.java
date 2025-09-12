package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.core.request.R;
import com.cool.modules.kiosk.entity.KioskMachineEntity;
import com.cool.modules.kiosk.service.KioskMachineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 打印设备
 */
@Tag(name = "打印设备", description = "打印设备")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO})
public class AdminKioskMachineController extends BaseController<KioskMachineService, KioskMachineEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }

    @Operation(summary = "激活")
    @GetMapping("/activate")
    public R<Long> activate(@RequestParam String key) {
        return R.ok(service.activate(key));
    }
}