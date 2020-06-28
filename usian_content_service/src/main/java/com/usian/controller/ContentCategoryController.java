package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.usian.utils.Result;
import java.util.List;
@RequestMapping("/service/contentCategory")
@RestController
public class ContentCategoryController {
    @Autowired//service层
    private ContentCategoryService contentCategoryService;

    //内容分类管理查询，根据parentId内查询
    @RequestMapping("/selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(Long id){
        return contentCategoryService.selectContentCategoryByParentId(id);
    }

    //内容分类管理添加
    @RequestMapping("/insertContentCategory")
    public int insertContentCategory(@RequestBody TbContentCategory tbContentCategory){
        return contentCategoryService.insertContentCategory(tbContentCategory);
    }

    //内容分类管理删除
    @RequestMapping("/deleteContentCategoryById")
    public int deleteContentCategoryById(Long categoryId){
        return contentCategoryService.deleteContentCategoryById(categoryId);
    }

    //内容分类管理修改
    @RequestMapping("/updateContentCategory")
    public int updateContentCategory(Long id,String name){
        return contentCategoryService.updateContentCategory(id,name);
    }

}
