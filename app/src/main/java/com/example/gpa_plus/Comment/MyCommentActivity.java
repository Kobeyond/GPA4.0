package com.example.gpa_plus.Comment;

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

public class MyCommentActivity extends BaseActivity {

    private RecyclerView myCommentsRV;
    private Context mContext;
    private ArrayList<Comment> commentArrayList=new ArrayList<>();
    public final int QUERY_MY_COMMENTS_SUCCESS=1;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);

        mContext=MyCommentActivity.this;
        myCommentsRV=(RecyclerView)findViewById(R.id.my_comments_RV);

         /*初始化标题栏*/
        toolbar=(Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        //   toolbar.setOnMenuItemClickListener(onMenuItemClick);

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_24dp);
        }

        queryMyComments();
    }
    public void queryMyComments(){

        BmobQuery<Comment> query=new BmobQuery<>();
        query.include("article").include("author").addWhereEqualTo("answerer", BmobUser.getCurrentUser().getObjectId()).findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        //背景换成空白？？
                        Toast.makeText(mContext,"你还没有回答过问题，赶紧去帮助别人吧！",Toast.LENGTH_SHORT).show();
                    }else{
                        Message msg=new Message();
                        msg.what=QUERY_MY_COMMENTS_SUCCESS;
                        msg.obj=list;
                        commentsHandler.sendMessage(msg);
                    }
                }else{
                    Toast.makeText(mContext,"获取回复失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void recyclerViewBind(List<Comment> commentList){
        MyCommentAdapter adapter=new MyCommentAdapter(commentList);
        myCommentsRV.setAdapter(adapter);
        StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        myCommentsRV.setLayoutManager(manager);
    }


    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler commentsHandler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                //若请求回复成功，则将数据源绑定至RV
                case QUERY_MY_COMMENTS_SUCCESS:
                    commentArrayList.clear();
                    commentArrayList=(ArrayList<Comment>)(msg.obj);
                    recyclerViewBind(commentArrayList);
                    break;
            }
        }
    };

    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                MyCommentActivity.this.finish();
                break;
        }
        return true;
    }

}


