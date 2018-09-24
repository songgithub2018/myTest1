package cn.itheima.jd.service.impl;

import cn.itheima.jd.po.Product;
import cn.itheima.jd.po.Result;
import cn.itheima.jd.service.SearchService;
import com.sun.media.sound.SoftLanczosResampler;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 搜索商品service实现类
 */
@Service
public class SearchServiceImpl implements SearchService {

    // 注入HttpSolrServer
    @Autowired
    private HttpSolrServer httpSolrServer;

    /**
     * 搜索商品
     * 参数确定：根据提交的搜索表单确定
     */
    public Result searchProduct(String queryString, String catalog_name, String price, Integer page, String sort) {

        // 1.建立查询对象（SolrQuery）
        SolrQuery sq = new SolrQuery();

        // 1.1.设置搜索关键词(如果搜索关键词为空，搜索全部)
        if(StringUtils.isNotBlank(queryString)){
            sq.setQuery(queryString);
        }else{
            sq.setQuery("*:*");
        }

        // 1.2.搜索默认搜索域
        sq.set("df","product_keywords");

        // 1.3.设置过滤条件
        // 商品分类名称
        if(StringUtils.isNotBlank(catalog_name)){
            catalog_name="product_catalog_name:"+catalog_name;
        }
        // 商品价格0-9
        if(StringUtils.isNotBlank(price)){
            String[] arr = price.split("-");
            price = "product_price:["+arr[0]+" TO "+arr[1]+"]";
        }
        sq.setFilterQueries(catalog_name,price);

        // 1.4.设置分页
        // 默认搜索第一页
        if(page== null){
            page=1;
        }

        int pageSize =10;// 每一页显示10条
        sq.setStart((page-1)*pageSize);
        sq.setRows(pageSize);

        // 1.5.设置排序
        // 如果是1，是升序，其它降序
        if("1".equals(sort)){
            sq.setSort("product_price", SolrQuery.ORDER.asc);
        }else{
            sq.setSort("product_price", SolrQuery.ORDER.desc);
        }

        // 1.6.设置高亮显示
        sq.setHighlight(true);//开启高亮显示
        sq.addHighlightField("product_name");
        sq.setHighlightSimplePre("<font color='red'>");// 设置高亮显示的html标签的开始部分
        sq.setHighlightSimplePost("</font>");// 设置高亮显示的html标签的结束部分

        // 2.执行搜索，返回QueryResponse
        QueryResponse queryResponse = null;
        try {
            queryResponse = httpSolrServer.query(sq);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        // 3.从QueryResposne中获取数据
        // 3.1.获取搜索结果集
        SolrDocumentList searchResult = queryResponse.getResults();

        // 3.2.获取高亮数据
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

        // 4.处理结果集
        // 4.1.创建Result
        Result result = new Result();

        // 4.2.设置当前页
        result.setCurPage(page);

        // 4.3.设置页数
        /**
         * 总记录数%页面大小；如果余数为0；pageCount=toltals/pageSize;
         * 如果余数不为0：pageCount=(toltals/pageSize)+1;
         */
        int toltals = (int)searchResult.getNumFound();
        int pageCount=0;
        if(toltals%pageSize==0){
            pageCount=toltals/pageSize;
        }else{
            pageCount=(toltals/pageSize)+1;
        }
        result.setPageCount(pageCount);

        // 4.4.设置总记录数
        result.setRecordCount(toltals);

        // 4.5.封装商品集合list
        List<Product> productList = new ArrayList<Product>();
        for(SolrDocument doc:searchResult){
            // 商品Id，商品名称，商品图片，商品价格
            String pid = doc.get("id").toString();

            String pname = "";
            List<String> list = highlighting.get(pid).get("product_name");
            if(list != null && list.size()>0){
                pname = list.get(0);
            }else{
                pname = doc.get("product_name").toString();
            }

            String picture = doc.get("product_picture").toString();

            String pprice = doc.get("product_price").toString();

            // 创建商品对象
            Product product = new Product();
            product.setPid(pid);
            product.setName(pname);
            product.setPicture(picture);
            product.setPrice(pprice);

            productList.add(product);
        }

        result.setProductList(productList);

        return result;
    }
}
