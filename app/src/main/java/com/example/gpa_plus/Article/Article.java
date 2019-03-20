package com.example.gpa_plus.Article;

import com.example.gpa_plus.MyUser;

import java.util.Date;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/*帖子类Article的Java Bean，用于映射Bmob数据库的Article表*/

public class Article extends BmobObject {

    //各个属性对应于数据库中Article表的各个列
    String title;  //帖子标题
    String content;  //帖子详细内容
    String label;  //帖子标签
    BmobFile photo;  //帖子的照片附件
    MyUser author;  //帖子的作者，指向用户表
    Integer count;  //评论数量


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
