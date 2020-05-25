package org.archer.mq.model;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.archer.mq.Message;
import org.archer.mq.constants.DeliverModeEnum;
import org.archer.mq.constants.Priorities;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
@Data
public class StandardMessage implements Message {

    private String id;

    private String key;

    private int deliverMode;

    private int priority;

    private Map<String, Object> properties;

    private Object body;

    private String desc;



    private StandardMessage(String id, String key, int deliverMode, int priority, Map<String, Object> properties, Object body, String desc) {
        this.id = id;
        this.key = key;
        this.deliverMode = deliverMode;
        this.priority = priority;
        this.properties = properties;
        this.body = body;
        this.desc = desc;
    }

    private StandardMessage() {
    }

    @Override
    public DeliverModeEnum deliverMode() {
        return DeliverModeEnum.of(deliverMode);
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public Map<String, Object> properties() {
        return properties;
    }

    @Override
    public Object body() {
        return body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements Message.Builder {


        private String key;

        private int deliverMode = DeliverModeEnum.TRANSIENT.getVal();

        private int priority = Priorities.LOW;

        private Map<String, Object> properties = Maps.newHashMap();

        private Object body;

        private String desc;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder deliverMode(DeliverModeEnum deliverMode) {
            if (Objects.nonNull(deliverMode)) {
                this.deliverMode = deliverMode.getVal();
            }
            return this;
        }

        public Builder priority(int priority) {
            if (priority >= Priorities.HIGH) {
                this.priority = Priorities.HIGH;
            } else this.priority = Math.max(priority, Priorities.LOW);
            return this;
        }

        /**
         * 不推荐使用该方式设置扩展属性，该方法会reset之前设置过的属性
         *
         * @param properties
         */
        public Builder properties(Map<String, Object> properties) {
            if (!CollectionUtils.isEmpty(properties)) {
                this.properties = properties;
            } else {
                this.properties = Maps.newHashMap();
            }
            return this;
        }

        public Builder addProperty(String key, Object val) {
            this.properties.put(key, val);
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Message build() {
            Assert.notNull(key, "message key not set");
            Assert.notNull(body, "message body not set");
            String id = DigestUtils.md5Hex(JSON.toJSONString(body) + key);
            return new StandardMessage(id, key, deliverMode, priority, properties, body, desc);
        }

    }
}
