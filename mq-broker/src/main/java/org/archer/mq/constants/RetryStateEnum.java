package org.archer.mq.constants;

public enum RetryStateEnum {
    RETRYING(0,"retrying"),
    WAIT_RETRY(1,"wait retry"),
    RETRY_FAILED(2,"retry failed");


    private int val;
    private String desc;

    RetryStateEnum(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

}
