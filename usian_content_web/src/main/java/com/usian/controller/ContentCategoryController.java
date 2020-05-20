package com.usian.controller;
import com.bjsxt.utils.Result;
import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/backend/content")
@RestController
public class ContentCategoryController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    //内容分类管理查询，根据parentId内查询，默认为0
    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id){
        List<TbContentCategory> tbContentCategoryList =  contentServiceFeign.selectContentCategoryByParentId(id);//查询出为list
        if(tbContentCategoryList.size()>0){//判断list是否为空
            return Result.ok(tbContentCategoryList);//返回list
        }
        return Result.error("查无结果！！！");//返回错误数据
    }
}
