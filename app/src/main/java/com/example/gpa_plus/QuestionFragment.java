package com.example.gpa_plus;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gpa_plus.Article.Article;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;


public class QuestionFragment extends Fragment implements View.OnClickListener{

    private Spinner spinner;
    private EditText titleText;
    private EditText detailText;
    private EditText courseText;
    private List<String> dataList;
    private ArrayAdapter<String> adapter;
    private ImageView choosePhotoBtn;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private FloatingActionButton publishBtn;
    private String path;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_answer, container, false);
        mContext=rootView.getContext();
        spinner=(Spinner)rootView.findViewById(R.id.answer_tag_spinner1);
        choosePhotoBtn=(ImageView)rootView.findViewById(R.id.answer_choose_photo_btn);
        choosePhotoBtn.setOnClickListener(this);
        publishBtn=(FloatingActionButton)rootView.findViewById(R.id.floatingActionButton);
        publishBtn.setOnClickListener(this);
        titleText=(EditText)rootView.findViewById(R.id.answer_title_text);
        detailText=(EditText)rootView.findViewById(R.id.answer_detail_text);
        courseText=(EditText)rootView.findViewById(R.id.answer_course_text);
        initSpinner();

        return rootView;
    }

    public void onClick(View view){
        switch (view.getId()){
            //点击按钮选择上传的图片
            case R.id.answer_choose_photo_btn:
                ChoosePhoto();
                break;

            case R.id.floatingActionButton:
                String title=titleText.getText().toString();
                String detail=detailText.getText().toString();
                String course=courseText.getText().toString();
                if(title.length()*detail.length()*course.length()==0){
                    Toast.makeText(this.getContext(),"请完善发帖内容再试！",Toast.LENGTH_SHORT).show();
                    return;
                }
                
                UploadArticle(path,title,detail,course);
                break;
        }
    }

    //初始化下拉框
    public void initSpinner(){
        dataList=new ArrayList<String>();
        dataList.add("文史类");
        dataList.add("理科类");
        dataList.add("工科类");
        adapter=new ArrayAdapter<String>(this.getContext(),R.layout.spinner_style,dataList);
        adapter.setDropDownViewResource(R.layout.spinner_expanded_style);
        spinner.setAdapter(adapter);
    }

    public void ChoosePhoto(){
         /*动态申请相册的访问权限*/
        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
        }else{
            openAlbum();
        }
    }

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override   /*拍照或选择照片返回后，进行相应的操作*/
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        switch (requestCode){

            /*如果是从相册选择照片返回*/
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    /*如果获取相册照片成功，再根据系统版本对照片进行不同地解析*/
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }

    }

    @TargetApi(19)  /*解析封装过的Uri对象，并展示在图片上*/
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();

        //以下将根据Uri的不同类型分情况进行解析
        if(DocumentsContract.isDocumentUri(this.getContext(),uri)){  /*如果是Document类型的Uri*/
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID +"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){  /*如果是content类型的Uri，则用普通方式处理*/
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){  /*如果是file类型的Uri，直接获取图片路径即可*/
            imagePath=uri.getPath();
        }

        displayImage(imagePath);
    }


    /*解析未封装过的Uri对象，并展示在图片上*/
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }


    /*通过Uri和selection来获取图片真实的路径*/
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=this.getContext().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    /*根据图像的路径，把它显示在ImageView控件上*/
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            choosePhotoBtn.setImageBitmap(bitmap);
            path=imagePath;
            //UploadPicture(imagePath);
            //如果点击发表，才会上传图片到服务器

        }else {
            Toast.makeText(this.getContext(),"获取图片失败!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override  /*动态获取系统权限*/
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this.getContext(),"您拒绝了权限申请",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //将头像上传至服务器
    public void UploadArticle(String picPath, final String title, final String detail, final String course){


        if(picPath == null){
            /*原理同修改数据一样，都是新建User对象，改变icon，然后调用update*/
            Article article=new Article();
            article.setAuthor(BmobUser.getCurrentUser(MyUser.class));
            article.setTitle(title);
            article.setContent(detail);
            article.setLabel(course);
            article.setCount(0);
            article.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    Toast.makeText(mContext,"发帖成功！",Toast.LENGTH_SHORT).show();
                    titleText.setText("");
                    detailText.setText("");
                    courseText.setText("");
                }
            });
            return;
        }

        final BmobFile icon=new BmobFile(new File(picPath));
        icon.uploadblock(new UploadFileListener(){
            @Override
            public void done(BmobException e){
                if(e==null){

                    /*原理同修改数据一样，都是新建User对象，改变icon，然后调用update*/
                    Article article=new Article();
                    article.setAuthor(BmobUser.getCurrentUser(MyUser.class));
                    article.setPhoto(icon);
                    article.setTitle(title);
                    article.setContent(detail);
                    article.setLabel(course);
                    article.setCount(0);
                    article.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Toast.makeText(mContext,"发帖成功！",Toast.LENGTH_SHORT).show();
                            titleText.setText("");
                            detailText.setText("");
                            courseText.setText("");
                        }
                    });

                }else{

                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
            }
        });
    }



}
