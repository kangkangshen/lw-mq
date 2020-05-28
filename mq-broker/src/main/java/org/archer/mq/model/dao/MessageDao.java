package org.archer.mq.model.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.archer.mq.Message;

@Mapper
public interface MessageDao {


    @Insert("insert into msg_content (msg_id, msg_key, msg_desc, msg_content, msg_properties)\n" +
            "values (#{id},#{key},#{desc},#{body},#{properties});")
    void insert(Message msg);


}
