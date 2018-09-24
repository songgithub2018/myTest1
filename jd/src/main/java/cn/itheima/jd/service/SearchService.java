package cn.itheima.jd.service;

import cn.itheima.jd.po.Result;

/**
 * 搜索service接口
 */
public interface SearchService {

    /**
     * 搜索商品
     * 参数确定：根据提交的搜索表单确定
     */
    Result searchProduct(String queryString,String catalog_name,String price,Integer page,String sort);
}
