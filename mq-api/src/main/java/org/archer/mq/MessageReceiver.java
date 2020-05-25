package org.archer.mq;

import org.apache.commons.lang3.StringUtils;
import org.archer.mq.annotations.Consumer;

@Consumer
public interface MessageReceiver {

    default String interestTopic() {
        return StringUtils.EMPTY;
    }

    default String interestQueue() {
        return StringUtils.EMPTY;
    }

    void onMsgReceive(Message msg);

    String consumerTag();

    String desc();
}
