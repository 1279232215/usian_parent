package com.usian.controller;

import com.usian.pojo.TbContent;
import com.usian.service.ContentService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/service/content")
@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;


    //内容分类查询
    @RequestMapping("/selectTbContentAllByCategoryId")
    public PageResult selectTbContentAllByCategoryId(Integer page,Integer rows,Long categoryId){
        return contentService.selectTbContentAllByCategoryId(page,rows,categoryId);
    }

    //内容分类添加
    @RequestMapping("/insertTbContent")
    public int insertTbContent(@RequestBody TbContent tbContent){
        return contentService.insertTbContent(tbContent);
    }

    //内容分类删除
    @RequestMapping("/deleteContentByIds")
    public int deleteContentByIds(Long id){
        return contentService.deleteContentByIds(id);
    }
}
