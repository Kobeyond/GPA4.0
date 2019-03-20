package com.example.gpa_plus.File;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gpa_plus.File.File;
import com.example.gpa_plus.File.FileAdapter;
import com.example.gpa_plus.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class FileFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private RecyclerView fileRV;
    private ImageView searchBtn;
    private EditText keywordText;
    private ArrayList<File> fileArrayList=new ArrayList<>();
    public final int QUERY_FILES_SUCCESS=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_file, container, false);

        mContext=this.getContext();
        fileRV=(RecyclerView)rootView.findViewById(R.id.file_rv);
        searchBtn=(ImageView)rootView.findViewById(R.id.file_search_btn);
        searchBtn.setOnClickListener(this);
        keywordText=(EditText)rootView.findViewById(R.id.file_keyword_text);

        queryFiles("all");

        return rootView;
    }

    public void queryFiles(String keyword){
        if(keyword.length()==0){
            Toast.makeText(mContext,"请输入搜索关键字",Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<File> query=new BmobQuery<>();
        if(keyword.equals("all")){
            query.include("owner");
        }else{
            query.addWhereEqualTo("tag",keyword).include("owner");
        }
        query.findObjects(new FindListener<File>() {
            @Override
            public void done(List<File> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        //背景换成空白？？
                        Toast.makeText(mContext,"当前暂无相应文件，赶紧去上传吧！",Toast.LENGTH_SHORT).show();
                    }else{
                        Message msg=new Message();
                        msg.what=QUERY_FILES_SUCCESS;
                        msg.obj=list;
                        filesHandler.sendMessage(msg);
                    }
                }else{
                    Toast.makeText(mContext,"获取资料失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.file_search_btn:
                String keyword=keywordText.getText().toString().trim();
                queryFiles(keyword);
                break;
        }
    }

    public void recyclerViewBind(List<File> fileList){
        FileAdapter adapter=new FileAdapter(fileList);
        fileRV.setAdapter(adapter);
        StaggeredGridLayoutManager manager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        fileRV.setLayoutManager(manager);
    }

    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler filesHandler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){

                //若请求帖子成功，则将数据源绑定至RV
                case QUERY_FILES_SUCCESS:
                    fileArrayList.clear();
                    fileArrayList=(ArrayList<File>)(msg.obj);
                    recyclerViewBind(fileArrayList);
                    break;
            }
        }
    };
}
