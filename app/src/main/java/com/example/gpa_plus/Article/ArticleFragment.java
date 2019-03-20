package com.example.gpa_plus.Article;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gpa_plus.Article.Article;
import com.example.gpa_plus.Article.ArticleAdapter;
import com.example.gpa_plus.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ArticleFragment extends Fragment {

    public ImageLoader imageLoader = ImageLoader.getInstance();
    private Context mContext;
    private ArrayList<Article> articleArrayList=new ArrayList<>();
    private RecyclerView articleRV;
    public final int QUERY_ARTICLES_SUCCESS=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_home, container, false);
        mContext=this.getContext();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        articleRV=(RecyclerView)rootView.findViewById(R.id.home_recyclerView);
        queryArticles();

        return rootView;
    }

    public void queryArticles(){
        BmobQuery<Article> query=new BmobQuery<>();
        query.include("author").findObjects(new FindListener<Article>() {
            @Override
            public void done(List<Article> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        //背景换成空白？？
                        Toast.makeText(mContext,"当前系统暂无帖子，赶紧去提问吧！",Toast.LENGTH_SHORT).show();
                    }else{
                        Message msg=new Message();
                        msg.what=QUERY_ARTICLES_SUCCESS;
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
        ArticleAdapter adapter=new ArticleAdapter(articleList);
        articleRV.setAdapter(adapter);
        StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        articleRV.setLayoutManager(manager);
    }


    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler articlesHandler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){

                //若请求帖子成功，则将数据源绑定至RV
                case QUERY_ARTICLES_SUCCESS:
                    articleArrayList.clear();
                    articleArrayList=(ArrayList<Article>)(msg.obj);
                    recyclerViewBind(articleArrayList);
                    break;
            }
        }
    };
}
