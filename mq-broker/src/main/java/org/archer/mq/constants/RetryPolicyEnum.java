package org.archer.mq.constants;

import java.util.Objects;

/**
 * 重试策略字符串常量
 */
public enum RetryPolicyEnum {
    RETRY_FOREVER(1, "RetryForever"),
    RETRY_ONE_TIME(2, "RetryOneTime"),
    RETRY_NTIMES(3, "RetryNTimes");


    private final Integer val;
    private final String desc;


    RetryPolicyEnum(Integer val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static RetryPolicyEnum of(int val) {
        RetryPolicyEnum[] enums = values();
        for (RetryPolicyEnum retryPolicyEnum : enums) {
            if (Objects.equals(retryPolicyEnum.val, val)) {
                return retryPolicyEnum;
            }
        }
        return null;
    }

    public static RetryPolicyEnum of(String retryPolicy) {
        RetryPolicyEnum[] enums = values();
        for (RetryPolicyEnum retryPolicyEnum : enums) {
            if (retryPolicyEnum.desc.equalsIgnoreCase(retryPolicy)) {
                return retryPolicyEnum;
            }
        }
        return null;
    }

    public Integer getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }
}
