package cn.itheima.jd.controller;

import cn.itheima.jd.po.Result;
import cn.itheima.jd.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 搜索处理器
 */
//又添加了注解
@Controller
public class SearchController {

    // 注入搜索service
    @Autowired
    private SearchService searchService;

    /**
     * 搜索商品
     * action="list.action"
     */
    @RequestMapping("list")
    public String list(Model model,String queryString,String catalog_name,String price,Integer page,String sort){

        // 1.搜索商品
        Result result = searchService.searchProduct(queryString, catalog_name, price, page, sort);

        // 2.响应数据
        model.addAttribute("result",result);

        // 3.参数数据回显
        model.addAttribute("queryString",queryString);
        model.addAttribute("catalog_name",catalog_name);
        model.addAttribute("price",price);
        model.addAttribute("sort",sort);

        return "product_list";
    }

}
