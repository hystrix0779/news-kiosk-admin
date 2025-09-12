package com.cool.modules.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    AWAITING_PAYMENT(0, "待付款"),
    AWAITING_DELIVERY(1, "待打印"),
    TRANSACTION_COMPLETE(4, "交易完成"),
    REFUND_IN_PROGRESS(5, "退款中"),
    REFUNDED(6, "已退款"),
    CLOSED(7, "已关闭"),
    REFUND_FAILED(8, "退款失败"),
    TRANSACTION_FAILED(9, "打印失败"),
    ;

    private final int code;
    private final String description;

    OrderStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static OrderStatusEnum fromCode(int code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
