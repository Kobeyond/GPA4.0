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
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpa_plus.Article.Article;
import com.example.gpa_plus.Comment.Comment;
import com.example.gpa_plus.Comment.CommentAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.gpa_plus.QuestionFragment.CHOOSE_PHOTO;

public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener{

    private String articleId;
    public static final int QUERY_COMMENTS_SUCCESS=1;
    public static final int SEND_COMMENT_SUCCESS=2;
    private ArrayList<Comment> commentArrayList=new ArrayList<>();
    private Toolbar toolbar;

    //以下是“楼主”相关的控件
    private TextView title;
    private CircleImageView ownerImg;
    private TextView ownerName;
    private ImageView ownerSex;
    private TextView label;
    private TextView content;
    private ImageView photo;

    //以下是“最佳回复”相关的控件
    private CircleImageView besterImg;
    private TextView besterName;
    private ImageView besterSex;
    private TextView besterFloor;
    private TextView besterContent;
    private ImageView besterPhoto;
    private TextView besterThumbs;

    //以下是“所有回复”相关的控件
    private RecyclerView commentsRV;

    private RelativeLayout layout;
    private RelativeLayout layout2;

    //以下是回复底部栏的相关控件
    private EditText remarkText;
    private ImageView remarkPhoto;
    private TextView remarkSend;

    private String path;
    private Context mContext;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defaulthead) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.defaulthead) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.defaulthead) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
            .build(); // 构建完成

    public void initWidgets(){

        //以下是“楼主”相关的控件
        title=(TextView)findViewById(R.id.detail_article_title);
        ownerImg=(CircleImageView)findViewById(R.id.detail_article_owner_head);
        ownerName=(TextView)findViewById(R.id.detail_article_name);
        ownerSex=(ImageView)findViewById(R.id.detail_article_sex);
        label=(TextView)findViewById(R.id.detail_article_label);
        content=(TextView)findViewById(R.id.detail_article_detail);
        photo=(ImageView)findViewById(R.id.detail_article_photo);

        //以下是“所有回复”相关的控件
        besterImg=(CircleImageView)findViewById(R.id.detail_bester_comment_head);
        besterName=(TextView)findViewById(R.id.detail_bester_comment_name);
        besterSex=(ImageView)findViewById(R.id.detail_bester_comment_sex);
        besterFloor=(TextView)findViewById(R.id.detail_bester_comment_floor);
        besterContent=(TextView)findViewById(R.id.detail_bester_comment_content);
        besterPhoto=(ImageView)findViewById(R.id.detail_bester_comment_photo);
        besterThumbs=(TextView)findViewById(R.id.detail_bester_comment_thumb_count);

        //以下是“所有回复”相关的控件
        commentsRV=(RecyclerView)findViewById(R.id.article_detail_commments_RV);

        layout=(RelativeLayout)findViewById(R.id.detail_bester_comment_layout);
        layout2=(RelativeLayout)findViewById(R.id.detail_bester_comment_layout2);
        mContext=ArticleDetailActivity.this;

        //以下是回复的底部栏控件
        remarkText=(EditText)findViewById(R.id.detail_remark_text);
        remarkPhoto=(ImageView)findViewById(R.id.detail_remark_photo);
        remarkSend=(TextView)findViewById(R.id.detail_remark_publish);
        remarkPhoto.setOnClickListener(this);
        remarkSend.setOnClickListener(this);

        /*初始化标题栏*/
        toolbar=(Toolbar)findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return_24dp);
        }

    }

    public void onClick(View view){
        switch (view.getId()){
            //点击图标，则从相册选择照片，同时替换图标？
            case R.id.detail_remark_photo:
                ChoosePhoto();
                break;

            //点击“发表”评论
            case R.id.detail_remark_publish:
                String content=remarkText.getText().toString().trim();
                if(content.length()==0){
                    Toast.makeText(mContext,"回复不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                remarkText.setText("");
                UploadComment(path,content);
                break;
        }
    }

    public void queryArticle(String articleId){
        BmobQuery<Article> query=new BmobQuery<Article>();
        query.include("author").getObject(articleId,new QueryListener<Article>() {
            @Override
            public void done(Article article,BmobException e) {
                if(e==null){

                    //加载原贴基本信息
                    title.setText(article.getTitle());
                    ownerName.setText(article.getAuthor().getUsername());
                    label.setText(article.getLabel());
                    content.setText(article.getContent());

                    //设置性别图标
                    String sex=article.getAuthor().getSex();
                    if(sex.equals("男")==true){
                        ownerSex.setImageResource(R.drawable.man);
                    }else{
                        ownerSex.setImageResource(R.drawable.woman);
                    }

                    //加载用户头像
                    BmobFile icon=article.getAuthor().getIcon();
                    if(icon!=null){
                        String imageUrl=icon.getFileUrl();
                        if(imageUrl.length()!=0){
                            ImageLoader imageLoader=ImageLoader.getInstance();
                            imageLoader.displayImage(imageUrl, ownerImg, options);
                        }
                    }

                    //加载用户上传的照片
                    BmobFile photoFile=article.getPhoto();
                    if(photoFile!=null){
                        String imageUrl=photoFile.getFileUrl();
                        if(imageUrl.length()!=0){
                            ImageLoader imageLoader=ImageLoader.getInstance();
                            imageLoader.displayImage(imageUrl, photo, options);
                        }else{
                            photo.setVisibility(View.GONE);
                        }
                    }else{
                        photo.setVisibility(View.GONE);
                    }


                }else{
                    Toast.makeText(mContext,"查询原贴失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void queryBestComment(String articleId){
        BmobQuery<Comment> query=new BmobQuery<>();
        query.include("article").include("answerer").order("-star").addWhereEqualTo("article",articleId).findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){

                        Toast.makeText(mContext,"当前暂无回复，赶紧帮帮他吧！",Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                        layout2.setVisibility(View.GONE);
                        besterPhoto.setVisibility(View.INVISIBLE);
                        besterContent.setVisibility(View.GONE);
                        besterFloor.setVisibility(View.GONE);
                        return;
                    }
                    //降序过后第一个元素就是最佳评论（赞最高）
                    Comment comment=list.get(0);
                    if(comment==null){
                        return;
                    }
                    //加载用户头像
                    BmobFile icon=comment.getAnswerer().getIcon();
                    if(icon!=null){
                        String imageUrl=icon.getFileUrl();
                        if(imageUrl.length()!=0){
                            ImageLoader imageLoader=ImageLoader.getInstance();
                            imageLoader.displayImage(imageUrl, besterImg, options);
                        }
                    }

                    //加载用户上传的照片
                    BmobFile photo=comment.getPhoto();
                    if(photo!=null){
                        String imageUrl=photo.getFileUrl();
                        if(imageUrl.length()!=0){
                            ImageLoader imageLoader=ImageLoader.getInstance();
                            imageLoader.displayImage(imageUrl, besterPhoto, options);
                        }
                    }else{
                        besterPhoto.setVisibility(View.GONE);
                    }

                    /*以下信息是可以根据comment直接获取并赋值的*/
                    besterName.setText(comment.getAnswerer().getUsername());
                    besterFloor.setText("#"+comment.getFloor());
                    besterContent.setText(comment.getContent());
                    besterThumbs.setText(""+comment.getStar());

                    //设置性别图标
                    String sex=comment.getAnswerer().getSex();
                    if(sex.equals("男")==true){
                        besterSex.setImageResource(R.drawable.man);
                    }else{
                        besterSex.setImageResource(R.drawable.woman);
                    }

                }else{
                    Toast.makeText(mContext,"获取评论失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Intent intent=getIntent();
        articleId=intent.getStringExtra("articleId");
        initWidgets();
        queryArticle(articleId);
        queryBestComment(articleId);
        queryComments();


    }


    //获取所有回复
    public void queryComments(){
        BmobQuery<Comment> query=new BmobQuery<>();
        query.include("article").include("answerer").order("floor").addWhereEqualTo("article",articleId).findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        Toast.makeText(mContext,"当前暂无回复，赶紧帮帮他吧！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Message msg=new Message();
                    msg.what=QUERY_COMMENTS_SUCCESS;
                    msg.obj=list;
                    commentsHandler.sendMessage(msg);

                }else{
                    Toast.makeText(mContext,"获取评论失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void recyclerViewBind(List<Comment> commentList){
        CommentAdapter adapter=new CommentAdapter(commentList);
        commentsRV.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        commentsRV.setLayoutManager(manager);

    }


    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler commentsHandler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                //若请求帖子成功，则将数据源绑定至RV
                case QUERY_COMMENTS_SUCCESS:
                    commentArrayList.clear();
                    commentArrayList=(ArrayList<Comment>)(msg.obj);
                    recyclerViewBind(commentArrayList);
                    break;

                //发表评论之后重新加载所有评论，并且原帖子回复数量加一
                case SEND_COMMENT_SUCCESS:
                    queryComments();
                    increaseCount();
                    break;
            }
        }
    };

    public void ChoosePhoto(){
         /*动态申请相册的访问权限*/
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
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
        if(DocumentsContract.isDocumentUri(mContext,uri)){  /*如果是Document类型的Uri*/
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
        Cursor cursor=mContext.getContentResolver().query(uri,null,selection,null,null);
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
            remarkPhoto.setImageBitmap(bitmap);
            path=imagePath;
            //UploadPicture(imagePath);
            //如果点击发表，才会上传图片到服务器

        }else {
            Toast.makeText(mContext,"获取图片失败!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override  /*动态获取系统权限*/
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(mContext,"您拒绝了权限申请",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //将头像上传至服务器
    public void UploadComment(String picPath, final String content){

        if(picPath == null){  //如果没有上传照片，则只设置回复内容和点赞数量等信息
            Comment comment=new Comment();
            comment.setContent(content);
            comment.setStar(0);
            comment.setAnswerer(BmobUser.getCurrentUser(MyUser.class));
            Article article=new Article();
            article.setObjectId(articleId);
            comment.setArticle(article);
            comment.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    Toast.makeText(mContext,"回复成功！",Toast.LENGTH_SHORT).show();
                    remarkText.setText("");
                    remarkPhoto.setImageResource(R.drawable.ic_comment_image_24dp);
                    Message msg=new Message();
                    msg.what=SEND_COMMENT_SUCCESS;
                    commentsHandler.sendMessage(msg);
                }
            });
            return;
        }

        //如果回复了照片
        final BmobFile icon=new BmobFile(new java.io.File(picPath));
        icon.uploadblock(new UploadFileListener(){
            @Override
            public void done(BmobException e){
                if(e==null){

                    Comment comment=new Comment();
                    comment.setContent(content);
                    comment.setStar(0);
                    comment.setAnswerer(BmobUser.getCurrentUser(MyUser.class));
                    Article article=new Article();
                    article.setObjectId(articleId);
                    comment.setArticle(article);
                    comment.setPhoto(icon);

                    comment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Toast.makeText(mContext,"回复成功！",Toast.LENGTH_SHORT).show();
                            remarkText.setText("");
                            remarkPhoto.setImageResource(R.drawable.ic_comment_image_24dp);
                            Message msg=new Message();
                            msg.what=SEND_COMMENT_SUCCESS;
                            commentsHandler.sendMessage(msg);
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


    public void increaseCount(){

        BmobQuery<Article> query=new BmobQuery<>();
        query.getObject(articleId, new QueryListener<Article>() {
            @Override
            public void done(Article article, BmobException e) {
                if(e==null){
                    Integer count=article.getCount()+1;
                    article.setCount(count);
                    article.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            //更新评论数量成功！
                        }
                    });
                }
            }
        });
    }

    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                ArticleDetailActivity.this.finish();
                break;
        }
        return true;
    }

}
