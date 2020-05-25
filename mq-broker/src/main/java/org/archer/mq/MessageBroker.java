package org.archer.mq;

import javax.annotation.Nonnull;

public interface MessageBroker {

    void deliverMsg(@Nonnull Message msg);

}
