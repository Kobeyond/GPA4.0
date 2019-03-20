package com.example.gpa_plus;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;


/*扩展的用户类的Java Bean，映射于Bmob的_User用户表*/

public class MyUser extends BmobUser {

    //扩展的用户属性：性别&用户头像
    String sex;
    BmobFile icon;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public BmobFile getIcon() {
        return icon;
    }

    public void setIcon(BmobFile icon) {
        this.icon = icon;
    }

}
