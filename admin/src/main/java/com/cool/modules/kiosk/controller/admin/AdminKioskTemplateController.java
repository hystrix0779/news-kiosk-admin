package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.core.request.R;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.entity.table.KioskTemplateEntityTableDef;
import com.cool.modules.kiosk.service.KioskTemplateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 打印模板
 */
@Tag(name = "打印模板", description = "打印模板")
@CoolRestController(api = { Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO })
public class AdminKioskTemplateController extends BaseController<KioskTemplateService, KioskTemplateEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setPageOption(createOp()
                .fieldEq(
                        KioskTemplateEntityTableDef.KIOSK_TEMPLATE_ENTITY.MACHINE_ID));
        setListOption(createOp()
                .fieldEq(
                        KioskTemplateEntityTableDef.KIOSK_TEMPLATE_ENTITY.MACHINE_ID));
    }

    @Operation(summary = "激活")
    @GetMapping("/infoById")
    public R<KioskTemplateEntity> infoById(@RequestParam Long id) {
        return R.ok((KioskTemplateEntity) service.info(id));
    }
}