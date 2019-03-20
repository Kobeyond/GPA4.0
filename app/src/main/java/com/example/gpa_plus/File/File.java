package com.example.gpa_plus.File;

import com.example.gpa_plus.MyUser;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;


/**
 * Created by dell on 2018/8/8.
 */

public class File extends BmobObject {

    MyUser owner;  //文件上传者
    BmobFile material;  //文件资料

    public File(File externalStorageDirectory, String filename) {
    }

    public MyUser getOwner() {
        return owner;
    }

    public void setOwner(MyUser owner) {
        this.owner = owner;
    }

    public BmobFile getMaterial() {
        return material;
    }

    public void setMaterial(BmobFile material) {
        this.material = material;
    }

}
