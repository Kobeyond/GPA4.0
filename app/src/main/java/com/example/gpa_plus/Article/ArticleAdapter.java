package com.example.gpa_plus.Article;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpa_plus.ArticleDetailActivity;
import com.example.gpa_plus.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;
import cn.bmob.v3.datatype.BmobFile;


/*自定义类Article的适配器，让Article对象作为RecyclerView的子项*/
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> mArticleList;

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defaulthead) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.defaulthead) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.defaulthead) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
            .build(); // 构建完成


    /*内部类ViewHolder用于容纳UI控件*/
    static class ViewHolder extends RecyclerView.ViewHolder{
        de.hdodenhof.circleimageview.CircleImageView headImg;
        TextView nameText;
        ImageView sexImg;
        TextView timeText;
        TextView labelText;
        TextView titleText;
        ImageView photoImg;
        TextView countText;
        View articleView;

        public ViewHolder(View view){
            super(view);
            headImg=(de.hdodenhof.circleimageview.CircleImageView)view.findViewById(R.id.item_article_head);
            nameText=(TextView)view.findViewById(R.id.item_article_name);
            sexImg=(ImageView)view.findViewById(R.id.item_article_sex);
            timeText=(TextView)view.findViewById(R.id.item_article_time);
            labelText=(TextView)view.findViewById(R.id.item_article_label_text);
            titleText=(TextView)view.findViewById(R.id.item_article_title);
            photoImg=(ImageView)view.findViewById(R.id.item_article_photo);
            countText=(TextView)view.findViewById(R.id.item_article_answer_count);
            articleView=view;
        }
    }

    /*自定义适配器的构造函数，用包含Article对象的数组作为参数*/
    public ArticleAdapter(List<Article> articleList){
        mArticleList=articleList;
    }

     @Override  /*用于创建viewHolder实例。先加载article_item布局，并作为参数view传给viewHolder*/
     public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article,parent,false);
        final ViewHolder holder =new ViewHolder(view);
         holder.articleView.setOnClickListener(new View.OnClickListener(){
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

        //加载用户头像
        BmobFile icon=article.getAuthor().getIcon();
        if(icon!=null){
            String imageUrl=icon.getFileUrl();
            if(imageUrl.length()!=0){
                ImageLoader imageLoader=ImageLoader.getInstance();
                imageLoader.displayImage(imageUrl, holder.headImg, options);
            }
        }

        //加载用户上传的照片
        BmobFile photo=article.getPhoto();
        if(photo!=null){
            String imageUrl=photo.getFileUrl();
            if(imageUrl.length()!=0){
                ImageLoader imageLoader=ImageLoader.getInstance();
                imageLoader.displayImage(imageUrl, holder.photoImg, options);
            }
        }else{
            holder.photoImg.setVisibility(View.GONE);
        }

         /*以下信息是可以根据article直接获取并赋值的*/
        holder.nameText.setText(article.getAuthor().getUsername());
        holder.labelText.setText(article.getLabel());
        holder.titleText.setText(article.getTitle());
        holder.countText.setText(article.getCount()+"");
        holder.timeText.setText(article.getCreatedAt().substring(0,10));

        //设置性别图标
        String sex=article.getAuthor().getSex();
        if(sex.equals("男")==true){
            holder.sexImg.setImageResource(R.drawable.man);
        }else{
            holder.sexImg.setImageResource(R.drawable.woman);
        }

    }


    @Override
    public int getItemCount(){
        return mArticleList.size();
    }

}
