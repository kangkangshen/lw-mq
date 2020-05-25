package org.archer.mq;

import org.archer.mq.constants.DeliverModeEnum;

import java.io.Serializable;
import java.util.Map;

public interface Message extends Serializable {

    /**
     * 返回当前消息投递模式
     *
     * @return 当前消息投递模式
     * @see DeliverModeEnum
     */
    DeliverModeEnum deliverMode();

    /**
     * 返回当前消息优先级
     *
     * @return 当前消息优先级
     * @see org.archer.mq.constants.Priorities
     */
    int priority();

    /**
     * 返回当前消息ID，该ID由对应消息发送端完成，上层应用不感知此ID
     *
     * @return 当前消息ID
     */
    String id();

    /**
     * 返回当前消息Key,该Key由对应消息产生端完成，对应的是某具体业务含义，比如交易单号，身份证号等等
     *
     * @return 当前消息key
     */
    String key();

    /**
     * 返回当前消息desc,用于日志记录或者其他数据统计
     *
     * @return 当前消息描述
     */
    String desc();


    /**
     * 返回当前消息属性列表，有些属性是消息发送端设置的，有些是上层应用设置的，对于服务器应当
     * 关系的Property
     *
     * @return 当前消息属性列表，当没有属性时，返回一个empty map
     * @see org.archer.mq.constants.PropertyKeys
     */
    Map<String, Object> properties();

    /**
     * 返回当前消息体
     *
     * @return 当前消息体
     */
    Object body();

    interface Builder {
        Message build();
    }
}
