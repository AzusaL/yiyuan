package com.team.azusa.yiyuan.bean;

public class JieXiaoDto {

    private String productId;                //商品ID
    private String proImgUrl;                //商品缩略图
    private String xianGou;                    //是否限购
    private int value;                        //商品价值
    private int yunNum;                        //云期数
    private long joinNum;                    //本云参与人次
    private String winnerId;                //获奖者ID
    private String winnerName;                //获奖者名字
    private String winnerImgUrl;            //头像
    private long jieXiaoTime;                //揭晓时间


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProImgUrl() {
        return proImgUrl;
    }

    public void setProImgUrl(String proImgUrl) {
        this.proImgUrl = proImgUrl;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getWinnerImgUrl() {
        return winnerImgUrl;
    }

    public void setWinnerImgUrl(String winnerImgUrl) {
        this.winnerImgUrl = winnerImgUrl;
    }

    public long getJieXiaoTime() {
        return jieXiaoTime;
    }

    public void setJieXiaoTime(long jieXiaoTime) {
        this.jieXiaoTime = jieXiaoTime;
    }

    public String getXianGou() {
        return xianGou;
    }

    public void setXianGou(String xianGou) {
        this.xianGou = xianGou;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getYunNum() {
        return yunNum;
    }

    public void setYunNum(int yunNum) {
        this.yunNum = yunNum;
    }

    public long getJoinNum() {
        return joinNum;
    }

    public void setJoinNum(long joinNum) {
        this.joinNum = joinNum;
    }
}
