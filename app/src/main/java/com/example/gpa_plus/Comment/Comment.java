package com.example.gpa_plus.Comment;

import com.example.gpa_plus.Article.Article;
import com.example.gpa_plus.MyUser;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/*评论类Comment的Java Bean，用于映射Bmob数据库的Comment表*/

public class Comment extends BmobObject {

    //各个属性对应于数据库中Comment表的各个列
    Integer star;  //点赞数量
    BmobFile photo;  //回帖附加的照片
    Integer floor;  //楼层数
    String content;   //回复内容
    Article article;  //该回复所对应的文章，指向Article表
    MyUser answerer;  //回复者，指向User表

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public MyUser getAnswerer() {
        return answerer;
    }

    public void setAnswerer(MyUser answerer) {
        this.answerer = answerer;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }
}
