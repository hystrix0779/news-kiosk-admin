package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.entity.table.KioskOrderEntityTableDef;
import com.cool.modules.kiosk.service.KioskOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单信息
 */
@Tag(name = "订单信息", description = "订单信息")
@CoolRestController(api = { Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO })
public class AdminKioskOrderController extends BaseController<KioskOrderService, KioskOrderEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setPageOption(createOp()
                .fieldEq(
                        KioskOrderEntityTableDef.KIOSK_ORDER_ENTITY.TEMPLATE_ID,
                        KioskOrderEntityTableDef.KIOSK_ORDER_ENTITY.MACHINE_ID,
                        KioskOrderEntityTableDef.KIOSK_ORDER_ENTITY.PAY_TYPE,
                        KioskOrderEntityTableDef.KIOSK_ORDER_ENTITY.STATUS));

    }
}