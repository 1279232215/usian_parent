package com.usian.service;

import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class TbItemParamServiceImpI implements TbItemParamService {
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    //查询商品规格模板
    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId); //设置条件
        List<TbItemParam> itemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);//因为selectByExample()默认描述是个大容量默认不查询，BlOBs是可以查描述了
        if(itemParams!=null && itemParams.size()>0){
            return itemParams.get(0);
        }
        return null;
    }
}
