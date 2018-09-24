package cn.itheima.jd.po;

/**
 * 商品实体类对象
 */
public class Product {

    // 商品Id
    private String pid;
    // 商品名称
    private String name;
    // 商品价格
    private String price;
    // 商品图片
    private String picture;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
