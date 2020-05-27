package com.usian.feign;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient("usian-content-service")
public interface ContentServiceFeign {
    /**
     *  @param id     parentId
     *  @return List<TbContentCategory>
     */
    @RequestMapping("/service/contentCategory/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long id);
    /**
     *  @param tbContentCategory     TbContentCategory
     *  @return int
     */
    @RequestMapping("/service/contentCategory/insertContentCategory")
    int insertContentCategory(@RequestBody TbContentCategory tbContentCategory);

    /**
     *  @param categoryId     TbContentCategory主id
     *  @return int
     */
    @RequestMapping("/service/contentCategory/deleteContentCategoryById")
    int deleteContentCategoryById(@RequestParam Long categoryId);

    /**
     *  @param id   TbContentCategory主id
     *  @param name
     *  @return int
     */
    @RequestMapping("/service/contentCategory/updateContentCategory")
    int updateContentCategory(@RequestParam Long id,@RequestParam String name);
    /**
     *  @param page   当前页
     *  @param rows    每页展示多少条
     *  @param categoryId   内容分类目id
     *  @return int
     */
    @RequestMapping("/service/content/selectTbContentAllByCategoryId")
    PageResult selectTbContentAllByCategoryId(@RequestParam Integer page,@RequestParam Integer rows,@RequestParam Long categoryId);

    /**
     *  @param tbContent 对象
     *  @return int
     */
    @RequestMapping("/service/content/insertTbContent")
    int insertTbContent(TbContent tbContent);

    /**
     *  @param id 对象
     *  @return int
     */
    @RequestMapping("/service/content/deleteContentByIds")
    int deleteContentByIds(@RequestParam Long id);

    /**
     *  @return List<AdNode>
     */
    @RequestMapping("/service/content/selectFrontendContentByAD")
    List<AdNode> selectFrontendContentByAD();
}
