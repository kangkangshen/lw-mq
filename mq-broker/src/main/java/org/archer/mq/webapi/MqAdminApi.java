package org.archer.mq.webapi;


import org.apache.commons.lang3.StringUtils;
import org.archer.mq.MqAdminService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("mqAdminApi")
@RequestMapping("/mqAdmin")
public class MqAdminApi {

    @Resource
    private MqAdminService mqAdminService;


    @PutMapping("/declareTopic")
    public void declareTopic(String topic, String desc) {
        Assert.isTrue(StringUtils.isNotBlank(topic), "topic name must not be blank");
        Assert.isTrue(StringUtils.isNotBlank(desc), "topic desc must not be blank");
        mqAdminService.declareTopic(topic, desc);
    }


    @DeleteMapping("/deleteTopic")
    public void deleteTopic(String topic) {
        Assert.isTrue(StringUtils.isNotBlank(topic), "topic name must not be blank");
        mqAdminService.deleteTopic(topic);
    }

    @PutMapping("/declareQueue")
    public void declareQueue(String queue, String desc) {
        Assert.isTrue(StringUtils.isNotBlank(queue), "queue name must not be blank");
        Assert.isTrue(StringUtils.isNotBlank(desc), "queue desc must not be blank");
        mqAdminService.declareTopic(queue, desc);
    }


    @DeleteMapping("/deleteQueue")
    public void deleteQueue(String queue) {
        Assert.isTrue(StringUtils.isNotBlank(queue), "topic name must not be blank");
        mqAdminService.deleteQueue(queue);
    }

}
