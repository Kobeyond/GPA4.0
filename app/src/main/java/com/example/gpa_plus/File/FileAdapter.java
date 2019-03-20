package com.example.gpa_plus.File;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpa_plus.R;

import java.util.List;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by dell on 2018/8/8.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<File> mFileList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fileImg;
        TextView fileNameText;
        TextView fileDateText;
        TextView fileSourceText;
        View fileView;

        public ViewHolder(View view){
            super(view);
            fileImg=(ImageView)view.findViewById(R.id.file_image);
            fileNameText=(TextView)view.findViewById(R.id.file_name);
            fileDateText=(TextView)view.findViewById(R.id.file_date);
            fileSourceText=(TextView)view.findViewById(R.id.file_source);
            fileView=view;
        }
    }

    public FileAdapter(List<File> fileList){
        mFileList=fileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewtype){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.fileView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int position=holder.getAdapterPosition();
                String name=mFileList.get(position).getMaterial().getFilename();
                String url=mFileList.get(position).getMaterial().getFileUrl();
                //fileName、group、url为参数构造BmobFile对象
                BmobFile bmobfile =new BmobFile(name,"",url);
                downloadFile(bmobfile,view.getContext());

            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,int position){
        File file=mFileList.get(position);

        //根据文件类型替换相应的图片
        String fileType=file.getMaterial().getFilename().split("\\.")[1];
        if(fileType.equals("doc")||fileType.equals("docx")){
            holder.fileImg.setImageResource(R.drawable.iconword);
        }else if(fileType.equals("pdf")){
            holder.fileImg.setImageResource(R.drawable.iconpdf);
        }else if(fileType.equals("xls")||fileType.equals("xlsx")){
            holder.fileImg.setImageResource(R.drawable.iconexcel);
        }else if(fileType.equals("ppt")||fileType.equals("pptx")){
            holder.fileImg.setImageResource(R.drawable.iconppt);
        }else if(fileType.equals("zip")){
            holder.fileImg.setImageResource(R.drawable.iconzip);
        }

        /*以下信息是可以根据article直接获取并赋值的*/
        holder.fileNameText.setText(file.getMaterial().getFilename());
        holder.fileDateText.setText(file.getCreatedAt().substring(5,10));
        holder.fileSourceText.setText(file.getOwner().getUsername());
    }


    private void downloadFile(BmobFile file, final Context context){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        java.io.File saveFile = new java.io.File(Environment.getExternalStorageDirectory()+"/bmob/", file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                Toast.makeText(context,"下载开始！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    Toast.makeText(context,"下载成功,保存路径为:"+savePath,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"下载失败："+e.getErrorCode()+","+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }


    @Override
    public int getItemCount(){
        return mFileList.size();
    }

}
