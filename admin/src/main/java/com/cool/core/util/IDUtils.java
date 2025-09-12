package com.cool.core.util;

import java.time.Instant;

import io.netty.util.internal.ThreadLocalRandom;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/07/31/14.
 * @annotation :
 */
public class IDUtils {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateDeviceCode() {
        // 当前时间戳（毫秒）转为36进制，通常11位左右
        String timestampPart = Long.toString(Instant.now().toEpochMilli(), 36).toUpperCase();

        // 需要补足到16位
        int remainingLength = 16 - timestampPart.length();
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < remainingLength; i++) {
            randomPart.append(CHAR_POOL.charAt(ThreadLocalRandom.current().nextInt(CHAR_POOL.length())));
        }

        // 合并并格式化成 XXXX-XXXX-XXXX-XXXX
        String fullCode = (timestampPart + randomPart).substring(0, 16);
        return formatDeviceCode(fullCode);
    }

    private static String formatDeviceCode(String raw) {
        return raw.replaceAll("(.{4})(?!$)", "$1-");
    }

}
