package com.example.gpa_plus.Comment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gpa_plus.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;
import cn.bmob.v3.datatype.BmobFile;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dell on 2018/8/10.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> mCommentList;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defaulthead) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.defaulthead) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.defaulthead) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
            .build(); // 构建完成


    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView headImg;
        TextView nameText;
        ImageView sexImg;
        TextView floorText;
        TextView contentText;
        ImageView photoImg;
        TextView thumbsText;
        View commentView;

        public ViewHolder(View view){
            super(view);
            headImg=(CircleImageView)view.findViewById(R.id.detail_comment_head);
            nameText=(TextView)view.findViewById(R.id.detail_comment_name);
            sexImg=(ImageView)view.findViewById(R.id.detail_comment_sex);
            floorText=(TextView)view.findViewById(R.id.detail_comment_floor);
            contentText=(TextView)view.findViewById(R.id.detail_comment_content);
            photoImg=(ImageView)view.findViewById(R.id.detail_comment_photo);
            thumbsText=(TextView)view.findViewById(R.id.detail_comment_thumb_count);
            commentView=view;
        }
    }


    public CommentAdapter(List<Comment> commentList){
        mCommentList=commentList;
    }

    @Override  /*用于创建viewHolder实例。先加载article_item布局，并作为参数view传给viewHolder*/
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_comment,parent,false);
        final ViewHolder holder =new ViewHolder(view);

        //设置监听事件········
        return holder;
    }

    @Override  /*用于子项的数据赋值。在每个子项滑动进入屏幕的时候执行*/
    public void onBindViewHolder(final ViewHolder holder,int position){
        Comment comment=mCommentList.get(position);
        if(comment==null){
            return;
        }

        //加载用户头像
        BmobFile icon=comment.getAnswerer().getIcon();
        if(icon!=null){
            String imageUrl=icon.getFileUrl();
            if(imageUrl.length()!=0){
                ImageLoader imageLoader=ImageLoader.getInstance();
                imageLoader.displayImage(imageUrl, holder.headImg, options);
            }
        }

        //加载用户上传的照片
        BmobFile photo=comment.getPhoto();
        if(photo!=null){
            String imageUrl=photo.getFileUrl();
            if(imageUrl.length()!=0){
                ImageLoader imageLoader=ImageLoader.getInstance();
                imageLoader.displayImage(imageUrl, holder.photoImg, options);
            }else{
                holder.photoImg.setVisibility(View.GONE);}
        }else{
            holder.photoImg.setVisibility(View.GONE);
        }

         /*以下信息是可以根据comment直接获取并赋值的*/
        holder.nameText.setText(comment.getAnswerer().getUsername());
        holder.floorText.setText("#"+comment.getFloor());
        holder.contentText.setText(comment.getContent());
        holder.thumbsText.setText(""+comment.getStar());

        //设置性别图标
        String sex=comment.getAnswerer().getSex();
        if(sex.equals("男")==true){
            holder.sexImg.setImageResource(R.drawable.man);
        }else{
            holder.sexImg.setImageResource(R.drawable.woman);
        }

    }

    @Override
    public int getItemCount(){
        return mCommentList.size();
    }

}
