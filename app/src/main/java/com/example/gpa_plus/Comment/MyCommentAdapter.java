package com.example.gpa_plus.Comment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gpa_plus.Article.Article;
import com.example.gpa_plus.ArticleDetailActivity;
import com.example.gpa_plus.R;

import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by dell on 2018/8/23.
 */

public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.ViewHolder>{
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView timeText;
        TextView starsText;
        TextView authorText;
        TextView articleText;
        TextView commentText;
        View commentView;

        public ViewHolder(View view){
            super(view);
            timeText=(TextView)view.findViewById(R.id.my_comment_time);
            starsText=(TextView)view.findViewById(R.id.my_comment_stars);
            authorText=(TextView)view.findViewById(R.id.my_comment_author);
            articleText=(TextView)view.findViewById(R.id.my_comment_article);
            commentText=(TextView)view.findViewById(R.id.my_comment_content);
            commentView=view;
        }

    }

    private List<Comment> mCommentList;
    private Context mContext;

    public MyCommentAdapter(List<Comment> commentList){
        mCommentList=commentList;
    }

    @Override  /*用于创建viewHolder实例。先加载myComment_item布局，并作为参数view传给viewHolder*/
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_comment,parent,false);
        final ViewHolder holder =new ViewHolder(view);
        holder.commentView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //点击子项，则跳转到帖子详情界面
                int position=holder.getAdapterPosition();
                Comment comment=mCommentList.get(position);
                String articleId=comment.getArticle().getObjectId();

                Intent intent=new Intent(view.getContext(),ArticleDetailActivity.class);
                intent.putExtra("articleId",articleId);
                view.getContext().startActivity(intent);
            }
        });

        return holder;
    }


    @Override  /*用于子项的数据赋值。在每个子项滑动进入屏幕的时候执行*/
    public void onBindViewHolder(final ViewHolder holder,int position){
        Comment comment=mCommentList.get(position);
        if(comment==null){
            return;
        }

         /*以下信息是可以根据article直接获取并赋值的*/
        holder.timeText.setText(comment.getCreatedAt().substring(0,10));
        holder.starsText.setText(comment.getStar()+"");
        holder.commentText.setText(comment.getContent());


        /*发帖人姓名要根据comment得到的articleId，去查询其对应的author，从而获得userName*/
        BmobQuery<Article> query=new BmobQuery<>();
        query.include("author").getObject(comment.getArticle().getObjectId(), new QueryListener<Article>() {
            @Override
            public void done(Article article, BmobException e) {
                holder.articleText.setText(article.getTitle());
                holder.authorText.setText(article.getAuthor().getUsername());
            }
        });

    }


    @Override
    public int getItemCount(){
        return mCommentList.size();
    }

}
