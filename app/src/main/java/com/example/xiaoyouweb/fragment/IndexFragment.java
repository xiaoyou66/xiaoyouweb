package com.example.xiaoyouweb.fragment;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;

import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


import com.example.xiaoyouweb.R;


import static android.content.Context.MODE_PRIVATE;



public class IndexFragment extends Fragment{
    private WebView mwebview;
    private Button mybtnon;
//    使用接口需要定义一个接口变量
    private showdata mcallback;
    private String[] titlelist=new String[100];
    private int stacklen=-1;//(这里使用栈来存储数据)
    private boolean back=false;//这个是布尔值用来判断是否按下了返回键
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_index,container,false);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);//这个要放在前面
        mybtnon=view.findViewById(R.id.btn_on);
        mwebview=view.findViewById(R.id.index_id);//这里要加一个view，就是在单前
        mwebview.getSettings().setJavaScriptEnabled(true);
        mwebview.getSettings().setDomStorageEnabled(true);//webview扩展api（加了这个就可以加载音乐了！）
        mwebview.setWebChromeClient(new mywebchromeclick());//对网页的一些设置
        mwebview.setWebViewClient(new mywebviewclick());
        mwebview.setDownloadListener(new DownloadListener() {
            //让浏览器下载我们需要的文件
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        setCookie();
        if(getArguments()!=null) mwebview.loadUrl(getArguments().getString("url"));
        //设置按钮点击事件
        mybtnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=mwebview.getUrl();
                Uri uri=Uri.parse(url);
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });



    }
    class mywebchromeclick extends WebChromeClient {
        private String data;
        //可以知道加载到什么进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            String s="正在加载:"+String.valueOf(newProgress);
            if(!back){if(newProgress==100){s=data;}}else{if(newProgress==100){s=pop();}}//如果加载完毕，那么就直接显示标题
            mcallback.showwebdata(s);//这里我们就实现了调用接口函数
//            mcallback.showwebdata(String.valueOf(stacklen));
            super.onProgressChanged(view, newProgress);
        }
        //可以得到标题
        @Override
        public void onReceivedTitle(WebView view, String title){
            data=title;
            if(!back) push(title);//这里点击了返回也会显示标题，所以最好就是在不点击的时候进栈
            back=false;//这里就是back的赋值好像只能放这这里了，要不然无法正常显示
            super.onReceivedTitle(view, title);
        }
    }
//    这里我们写一个方法,来让网页在当前webview跳转
    class mywebviewclick extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());//我们让新的链接直接在我们的当前webview上显示
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
//    自己写一个接口用来和activity进行通信
    public interface showdata{
        void showwebdata(String s);//这里我们设置一个方法
    }
    //这里实例化接口
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mcallback=(showdata)context;
        }catch (ClassCastException e){
            throw  new ClassCastException("主activity需要实现该方法");
        }
    }
    /*重写父类的onBackPressed*/
    public boolean onBackPressed() {
        back=true;
        mwebview.goBack();
       if(stacklen!=0){return true;}
       else{return false;}
    }
//下面是入栈和出栈的函数,用来实现网页标题正常点击效果
    private void push(String title)
    {
        if(stacklen!=100)
        {
            titlelist[++stacklen] =title;
        }
    }
    private String pop()
    {
        if(stacklen!=0)
        {
            return titlelist[--stacklen];
        }
        return " ";
    }
    //这里写一个activity和fragment通信的方法
    public static IndexFragment newInstance(String title){//这样就可以重写方法了
        IndexFragment fragment=new IndexFragment();
        Bundle bundle =new Bundle();
        bundle.putString("url",title);
        fragment.setArguments(bundle);//通过这样一种方法，就可以绑定参数，这样即使被回收也会重新赋值
        return fragment;
    }


    private void setCookie() {
        SharedPreferences myprofie=getContext().getSharedPreferences("cookie" , MODE_PRIVATE);
        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeSessionCookies(null);
//        cookieManager.flush();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookies(null);
//        Log.d("@@------",cookieManager.getCookie("https://xiaoyou66.com"));
        String a=myprofie.getString("cookie"," ");
        if(a!=" ") {
            cookieManager.setCookie("https://xiaoyou66.com", a.substring(a.indexOf("wordpress_test_cookie")));//
            cookieManager.setCookie("https://xiaoyou66.com", a.substring(a.indexOf("wordpress_sec")));
            cookieManager.setCookie("https://xiaoyou66.com", a.substring(a.indexOf("wordpress_logged_in")));//
        }

    }
}
