package org.archer.mq.constants;

import java.util.Objects;

public enum DeliverModeEnum {
    TRANSIENT(0, "store in memory"),
    PERSISTENT(1, "store in db");

    private final int val;
    private final String desc;

    DeliverModeEnum(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static DeliverModeEnum of(Integer val) {
        DeliverModeEnum[] deliverModeEnums = values();
        for (DeliverModeEnum deliverModeEnum : deliverModeEnums) {
            if (Objects.equals(deliverModeEnum.val, val)) {
                return deliverModeEnum;
            }
        }
        return null;
    }

    public int getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }

}
