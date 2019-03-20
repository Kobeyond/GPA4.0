package com.example.gpa_plus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gpa_plus.Article.ArticleFragment;
import com.example.gpa_plus.Article.MyArticleActivity;
import com.example.gpa_plus.Comment.MyCommentActivity;
import com.example.gpa_plus.File.FileFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public ImageLoader imageLoader = ImageLoader.getInstance();

    private Button testBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this,"9fc9a5ff9ccec8f2d57987616c033519");
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        //?????????????????????
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            // 允许用户使用应用
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
        }

        //测试-------------------------------------------------------------
        testBtn=(Button)findViewById(R.id.test_user_btn);
        testBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(MainActivity.this,"点击成功！",Toast.LENGTH_SHORT).show();
                BmobUser.loginByAccount("Kobeyond", "1997422", new LogInListener<MyUser>() {
                    @Override
                    public void done(MyUser user, BmobException e) {
                        if(user!=null){
                            Toast.makeText(MainActivity.this,"用户登录成功！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        //-------------------------------------------------------------------

        initWidgets();


    }

    public void initWidgets(){
        /*初始化标题栏*/
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
     //   toolbar.setOnMenuItemClickListener(onMenuItemClick);

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_menu_24dp);
        }


        drawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        navigationView = (NavigationView) findViewById(R.id.navi);
        navigationView.setNavigationItemSelectedListener(this);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //初始时，设置主页为fragment的内容
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new ArticleFragment();
        transaction.replace(R.id.fragment,fragment);
        transaction.commit();
    }

    @Override   /*设置侧滑菜单的监听事件*/
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case "我的提问":
                Intent intent = new Intent(MainActivity.this, MyArticleActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;

            case "我的回答":
                Intent intent2 = new Intent(MainActivity.this, MyCommentActivity.class);
                startActivity(intent2);
                drawerLayout.closeDrawer(navigationView);
                return true;

            case "注销":
                ActivityCollector.finishAll();
                break;
        }
        return true;
    }


    //设置底部栏的监听事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = null;

            switch (item.getItemId()) {
                //点击任意底部栏按钮，则切换fragment
                case R.id.navigation_home:
                    fragment = new ArticleFragment();
                    break;

                case R.id.navigation_index:
                    fragment=new FileFragment();
                    break;

                case R.id.navigation_answer:
                    fragment=new QuestionFragment();
                    break;
            }
            transaction.replace(R.id.fragment,fragment);
            transaction.commit();
            return true;
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

             /*点击标题栏左边按钮，则展开侧滑菜单*/
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

        }
        return true;
    }

}
