package com.usian.service;

import com.usian.mapper.DeDuplicationMapper;
import com.usian.pojo.DeDuplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class DeDuplicationServiceImpI implements DeDuplicationService {

    @Autowired
    private DeDuplicationMapper deDuplicationMapper;


    //查询消息去重表，看是逻辑是否被执行过
    @Override
    public DeDuplication selectDeDuplicationByTxNo(String txNo) {
        return deDuplicationMapper.selectByPrimaryKey(txNo);
    }

    @Override
    public void insertDeDuplication(String txNo) {
        DeDuplication deDuplication1 = new DeDuplication();
        deDuplication1.setTxNo(txNo);
        deDuplication1.setCreateTime(new Date());
        deDuplicationMapper.insertSelective(deDuplication1);
    }
}
