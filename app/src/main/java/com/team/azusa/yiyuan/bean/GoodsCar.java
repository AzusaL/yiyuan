package com.team.azusa.yiyuan.bean;

/**
 * Created by Azusa on 2016/1/25.
 */
public class GoodsCar {
    private ProductDto productDto;
    private int buyCount;

    public GoodsCar() {
    }

    public GoodsCar(ProductDto productDto, int buyCount) {
        this.productDto = productDto;
        this.buyCount = buyCount;
    }

    public ProductDto getProductDto() {
        return productDto;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    public void addBuyCount() {
        this.buyCount++;
    }
}
