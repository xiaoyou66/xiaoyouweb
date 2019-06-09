package com.example.xiaoyouweb.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xiaoyouweb.R;
import com.example.xiaoyouweb.other.Clearcache;

import java.io.File;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;
import static com.example.xiaoyouweb.network.Login.LoginByPost;
import static com.example.xiaoyouweb.network.Login.updatemessage;


public class PersonFragment extends Fragment {
    private Button about;
    private ImageView useimage;
    private TextView nickname;
    private TextView sign;
    private Button logout;
    private Button change;
    private TextView login;
    private String usr;
    private String pwd;
    private Button clear;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_person,container,false);
//        new Thread()
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login=view.findViewById(R.id.tx_login);
        about=view.findViewById(R.id.btn_about);
        clear=view.findViewById(R.id.btn_clear);
        update();//进来后默认更新一下界面
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里点击一下弹出新窗口
                final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());//新建一个builder
                View view1=LayoutInflater.from(getActivity()).inflate(R.layout.layout_login,null);//新建一个视图
                final EditText usrname=view1.findViewById(R.id.ed_usr);
                final EditText passwd=view1.findViewById(R.id.ed_pass);//final是成员变量或者本地变量(在方法中的或者代码块中的变量称为本地变量)声明（没看大懂）
                Button login=view1.findViewById(R.id.btn_login);
                Button regin=view1.findViewById(R.id.btn_regin);
                Button forget=view1.findViewById(R.id.btn_forget);
                final AlertDialog dialog1=builder.setTitle("注册/登录").setView(view1).show();
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        usr=usrname.getText().toString();
                        pwd=passwd.getText().toString();
                        new Thread(networkstack).start();
                        Toast.makeText(getActivity(),"正在登录.....",Toast.LENGTH_LONG).show();
                        dialog1.dismiss();
                    }
                });
                regin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri=Uri.parse("https://xiaoyou66.com/wp-login.php?action=register");
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);
                        dialog1.dismiss();
                    }
                });
                forget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri=Uri.parse("https://xiaoyou66.com/wp-login.php?action=lostpassword");
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);
                        dialog1.dismiss();
                    }
                });

            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
                builder.setTitle("关于本软件" ) ;
                builder.setMessage("当前版本:V1.1\n更新时间:2019/6/9\n有问题可以到小游网的关于博主板块进行反馈！" ) ;
                builder.setPositiveButton("我知道了" ,  null );
                builder.show();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
                builder.setTitle("清除缓存" ) ;
                String size="0.0k";
                try {
                    size= Clearcache.getTotalCacheSize(getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                builder.setMessage("当前缓存为"+size+"确定清除？(不会清除你的登录数据)") ;
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Clearcache.clearAllCache(getContext());
                        Toast.makeText(getActivity(),"清理完毕！",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                退出登录操作，其实就是把数据清除，然后在更新数据
                SharedPreferences myprofie= Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo" , MODE_PRIVATE);
                SharedPreferences cookie= Objects.requireNonNull(getActivity()).getSharedPreferences("cookie" , MODE_PRIVATE);
                myprofie.edit().clear().apply();
                cookie.edit().clear().commit();//把cookie和个人数据都清除
                update();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {
                //修改个人资料
                final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());//新建一个builder
                final View view1=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_index,null);//新建一个视图
                final WebView webView;
                Button mybutton;
                mybutton=view1.findViewById(R.id.btn_on);
                mybutton.setVisibility(View.GONE);
                webView=view1.findViewById(R.id.index_id);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new mywebviewclick());
                webView.setWebChromeClient(new MyWebChromeClient());
                //上面那些都是webview的一些设置
                SharedPreferences myprofie=getContext().getSharedPreferences("cookie" , MODE_PRIVATE);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookies(null);
                String a=myprofie.getString("cookie"," ");
                cookieManager.setCookie("https://xiaoyou66.com",a.substring(a.indexOf("wordpress_test_cookie")));//
                cookieManager.setCookie("https://xiaoyou66.com",a.substring(a.indexOf("wordpress_sec")));
                cookieManager.setCookie("https://xiaoyou66.com",a.substring(a.indexOf("wordpress_logged_in")));//
                webView.loadUrl("https://xiaoyou66.com/wp-admin/profile.php");
                Dialog dialog=builder.setTitle("修改个人资料").setView(view1).show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);;
            }
        });
    }
    //http请求Handel，用来与主线程通信
    @SuppressLint("HandlerLeak")//这个是标注忽略指定的警告
    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data=msg.getData();//Bundle类似于Python的字典
//            System.out.println("头像："+data.getString("pic"));
            if(data.getString("name")==null) Toast.makeText(getActivity(),"登录失败,请检查用户名或密码是否正确！",Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(getActivity(),"登录成功！",Toast.LENGTH_SHORT).show();
                //这里我们把数据存储起来
                //fragment不能直接调用sharedpreference方法
                SharedPreferences myprofie=Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo" , MODE_PRIVATE);
                myprofie.edit().clear().apply();
                myprofie.edit().putString("name",data.getString("name"))
                                .putString("email",data.getString("email"))
                                .putString("sign",data.getString("sign"))
                                .putString("pic",data.getString("pic"))
                                .putString("islogin","1").apply();
                update();
            }

        }
    };
    //这里我们使用子线程来进行http请求
    Runnable networkstack=new Runnable() {
        @Override
        public void run() {
            //我们把网页请求代码放到子线程里面(数据部分发给Handel)
            Bundle data=new Bundle();
            try {
               data=LoginByPost(usr,pwd,getContext());
            }catch (Exception e){
                e.printStackTrace();
            }
            Message a=new Message();
            a.setData(data);
            //这里不能放UI操作相关的内容
//                Log.d("网页数据",msg);
            handler.sendMessage(a);
        }
    };
    //更新自己的信息也单独放到一个线程里面
    Runnable update=new Runnable() {
        @Override
        public void run() {
            updatemessage(getActivity());
        }
    };

    private void update()//更新个人信息
    {
        new Thread(update).start();
        SharedPreferences myprofie=Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo" , MODE_PRIVATE);
        change=getView().findViewById(R.id.btn_change);
        logout=getView().findViewById(R.id.btn_logout);
        useimage=getView().findViewById(R.id.img);
        nickname=getView().findViewById(R.id.tx_nickname);
        sign=getView().findViewById(R.id.tx_sign);
        if(Objects.requireNonNull(myprofie.getString("islogin", "0")).equals("1")){
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            change.setVisibility(View.VISIBLE);
            nickname.setVisibility(View.VISIBLE);
            sign.setVisibility(View.VISIBLE);
            nickname.setText(myprofie.getString("name",""));
            String signtext=myprofie.getString("sign"," ");
            //if判断相等的那部分发生很大变化
            if(Objects.equals(signtext, " ")) signtext="这个家伙懒死了，还没有签名呢";
            sign.setText(signtext);
//            RequestOptions requestOptions = new RequestOptions();//新版的设置方面有很大变化
//            requestOptions.override(80, 80);//这个是设置，感觉好像没什么用
            Glide.with(getActivity()).load(myprofie.getString("pic"," ")).into(useimage);
        }else{
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            change.setVisibility(View.GONE);
            nickname.setVisibility(View.GONE);
            sign.setVisibility(View.GONE);
            useimage.setImageResource(R.mipmap.avatar);//记住了，这个才是修改头像的！！
        }
    }



    //设置网页在本页显示(这个是网页点击连接事件)
    class mywebviewclick extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());//我们让新的链接直接在我们的当前webview上显示
            return super.shouldOverrideUrlLoading(view, request);
        }
    }


//下面都是文件上传代码(我完全看不懂。。。。。)
private static ValueCallback<Uri> mUploadMessage;
    private static ValueCallback<Uri[]> mUploadMessageLollipop;

    private class MyWebChromeClient extends WebChromeClient {

        // Android 4.*（包括4.1、4.2、4.3、4.4）
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            Log.d(TAG, "openFileChooser 4.*");
            mUploadMessage = uploadMsg;
            openSelectDialog();
        }

        // Android 5.0+（包括5.*、6.0、7.*、8.*）
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            Log.d(TAG, "openFileChooser 5.0+");
            mUploadMessageLollipop = filePathCallback;
            openSelectDialog();
            return true;
        }
    }
    private String mCameraPhotoPath = null;

    private void openSelectDialog() {
        // 声明相机的拍照行为
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            mCameraPhotoPath = "file:" + getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + "/" +"1"+ ".jpg";
            Log.d(TAG, "photoFile=" + mCameraPhotoPath);
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(mCameraPhotoPath));
        }
        Intent[] intentArray = new Intent[] { photoIntent };
        // 声明相册的打开行为
        Intent selectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        selectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        selectionIntent.setType("image/*");
        // 弹出含相机和相册在内的列表对话框
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, selectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "请拍照或选择图片");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(Intent.createChooser(chooserIntent, "选择图片"), 1);
    }
    private static final int FILE_SELECT_CODE = 1;
    private int mResultCode = Activity.RESULT_CANCELED;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode != FILE_SELECT_CODE
                || (mUploadMessage == null && mUploadMessageLollipop == null)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        mResultCode = resultCode;
        Log.d(TAG, "mCameraPhotoPath=" + mCameraPhotoPath);
        if (resultCode == Activity.RESULT_OK) {
            uploadPhoto(resultCode, data);
        }
    }

    private void uploadPhoto(int resultCode, Intent data) {
        long fileSize = 0;
        try {
            String file_path = mCameraPhotoPath.replace("file:", "");
            File file = new File(file_path);
            fileSize = file.length();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data != null || mCameraPhotoPath != null) {
            Integer count = 1;
            ClipData images = null;
            try {
                images = data.getClipData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (images == null && data != null && data.getDataString() != null) {
                count = data.getDataString().length();
            } else if (images != null) {
                count = images.getItemCount();
            }
            Uri[] results = new Uri[count];
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "fileSize=" + fileSize);
                if (fileSize != 0) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[] { Uri.parse(mCameraPhotoPath) };
                    }
                } else if (data.getClipData() == null) {
                    results = new Uri[] { Uri.parse(data.getDataString()) };
                } else {
                    for (int i = 0; i < images.getItemCount(); i++) {
                        results[i] = images.getItemAt(i).getUri();
                    }
                }
            }
            // 区分不同系统分别返回上传结果
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessageLollipop.onReceiveValue(results);
                mUploadMessageLollipop = null;
            } else {
                mUploadMessage.onReceiveValue(results[0]);
                mUploadMessage = null;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // 取消选择时需要回调onReceiveValue，否则网页会挂住，不会再响应点击事件
        if (mResultCode == Activity.RESULT_CANCELED) {
            try {
                if (mUploadMessageLollipop != null) {
                    mUploadMessageLollipop.onReceiveValue(null);
                }
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
