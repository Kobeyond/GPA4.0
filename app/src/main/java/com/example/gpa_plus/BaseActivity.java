package com.example.gpa_plus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dell on 2018/8/24.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);//创建一个新活动时加入活动管理器
    }

    //重写onDestroy()从活动管理器中移除被关闭的活动
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}