package org.archer.mq.model.mng.impl;


import org.archer.mq.Message;
import org.archer.mq.model.dao.MessageDao;
import org.archer.mq.model.mng.MsgManager;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class MsgManagerImpl implements MsgManager {

    @Resource
    private MessageDao messageDao;


    @Override
    public void saveMsg(Message msg) {
        messageDao.insert(msg);
    }
}
