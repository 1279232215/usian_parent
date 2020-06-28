package com.usian.controller;

import com.usian.utils.Result ;
import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/backend/content")
@RestController
public class ContentController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;


    //内容查询接口
    @RequestMapping("/selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(@RequestParam(defaultValue = "1")Integer page,
                                                 @RequestParam(defaultValue = "30")Integer rows,
                                                 Long categoryId)
    {
        PageResult pageResult = contentServiceFeign.selectTbContentAllByCategoryId(page,rows,categoryId);
        if(pageResult.getResult()!=null && pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("内容管理查询失败！！！");
    }

    //内容添加接口
    @RequestMapping("/insertTbContent")
    public Result insertTbContent(TbContent tbContent){
        int i = contentServiceFeign.insertTbContent(tbContent);
        if(i==1){
            return Result.ok();
        }
        return Result.error("内容添加失败!!!");
    }

    //内容删除接口
    @RequestMapping("/deleteContentByIds")
    public Result deleteContentByIds(@RequestParam(value="ids")Long id){
        int i = contentServiceFeign.deleteContentByIds(id);
        if(i==1){
            return Result.ok();
        }
        return Result.error("内容删除失败!!!");
    }
}
