package com.example.gpa_plus.Article;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gpa_plus.BaseActivity;
import com.example.gpa_plus.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyArticleActivity extends BaseActivity {

    private RecyclerView myArticleRV;
    private Context mContext;
    private ArrayList<Article> articleArrayList=new ArrayList<>();
    public final int QUERY_MY_ARTICLES_SUCCESS=1;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_article);

        mContext=MyArticleActivity.this;
        myArticleRV=(RecyclerView)findViewById(R.id.my_articles_RV);

        /*初始化标题栏*/
        toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        //   toolbar.setOnMenuItemClickListener(onMenuItemClick);

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_24dp);
        }


        queryMyArticles();
    }

    public void queryMyArticles(){

        BmobQuery<Article> query=new BmobQuery<>();
        query.addWhereEqualTo("author",BmobUser.getCurrentUser().getObjectId()).findObjects(new FindListener<Article>() {
            @Override
            public void done(List<Article> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        //背景换成空白？？
                        Toast.makeText(mContext,"当前系统暂无帖子，赶紧去提问吧！",Toast.LENGTH_SHORT).show();
                    }else{
                        Message msg=new Message();
                        msg.what=QUERY_MY_ARTICLES_SUCCESS;
                        msg.obj=list;
                        articlesHandler.sendMessage(msg);
                    }
                }else{
                    Toast.makeText(mContext,"获取帖子失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void recyclerViewBind(List<Article> articleList){
        MyArticleAdapter adapter=new MyArticleAdapter(articleList);
        myArticleRV.setAdapter(adapter);
        StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        myArticleRV.setLayoutManager(manager);
    }


    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler articlesHandler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){

                //若请求帖子成功，则将数据源绑定至RV
                case QUERY_MY_ARTICLES_SUCCESS:
                    articleArrayList.clear();
                    articleArrayList=(ArrayList<Article>)(msg.obj);
                    recyclerViewBind(articleArrayList);
                    break;
            }
        }
    };

    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                MyArticleActivity.this.finish();
                break;
        }
        return true;
    }
}
