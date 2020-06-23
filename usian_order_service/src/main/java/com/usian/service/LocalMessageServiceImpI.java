package com.usian.service;

import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.LocalMessageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocalMessageServiceImpI implements LocalMessageService{

    @Autowired
    private LocalMessageMapper localMessageMapper;



    //查询本地消息表,状态为0没有发送成功的
    @Override
    public List<LocalMessage> selectlocalMessageByStatus(int state) {
        LocalMessageExample localMessageExample = new LocalMessageExample();
        LocalMessageExample.Criteria criteria = localMessageExample.createCriteria();
        criteria.andStateEqualTo(state);
        List<LocalMessage> localMessages = localMessageMapper.selectByExample(localMessageExample);
        return localMessages;
    }
}
