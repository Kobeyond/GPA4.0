package com.example.gpa_plus.Article;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gpa_plus.ArticleDetailActivity;
import com.example.gpa_plus.R;

import java.util.List;

/**
 * Created by dell on 2018/8/22.
 */

public class MyArticleAdapter extends RecyclerView.Adapter<MyArticleAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleText;
        TextView timeText;
        TextView countText;
        View myArticleView;

        public ViewHolder(View view){
            super(view);
            titleText=(TextView)view.findViewById(R.id.my_article_title);
            timeText=(TextView)view.findViewById(R.id.my_article_time_label);
            countText=(TextView)view.findViewById(R.id.my_article_count);
            myArticleView=view;
        }
    }

    private List<Article> mArticleList;

    public MyArticleAdapter(List<Article> articleList){
        mArticleList=articleList;
    }

    @Override  /*用于创建viewHolder实例。先加载article_item布局，并作为参数view传给viewHolder*/
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_article,parent,false);
        final ViewHolder holder =new ViewHolder(view);
        holder.myArticleView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //点击子项，则跳转到帖子详情界面
                int position=holder.getAdapterPosition();
                Article article=mArticleList.get(position);
                String articleId=article.getObjectId();
                Intent intent=new Intent(view.getContext(),ArticleDetailActivity.class);
                intent.putExtra("articleId",articleId);
                view.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override  /*用于子项的数据赋值。在每个子项滑动进入屏幕的时候执行*/
    public void onBindViewHolder(final ViewHolder holder,int position){
        Article article=mArticleList.get(position);
        if(article==null){
            return;
        }

         /*以下信息是可以根据article直接获取并赋值的*/
        holder.titleText.setText(article.getTitle());
        holder.timeText.setText(article.getCreatedAt().substring(0,10)+"  "+article.getLabel());
        holder.countText.setText(article.getCount()+"");

    }


    @Override
    public int getItemCount(){
        return mArticleList.size();
    }


}
