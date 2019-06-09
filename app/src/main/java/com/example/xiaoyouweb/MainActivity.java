package com.example.xiaoyouweb;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;


import com.example.xiaoyouweb.fragment.IndexFragment;
import com.example.xiaoyouweb.fragment.PersonFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IndexFragment.showdata{//这里我们继承那个接口
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };//上面这些都是动态申请权限
    private IndexFragment indexfragment;//这个就是主页的fragment
    private PersonFragment personFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    indexfragment=IndexFragment.newInstance("https://xiaoyou66.com/");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,indexfragment).commitAllowingStateLoss();
//                这里才是写的重点
                    return true;
                case R.id.navigation_dashboard:
                    indexfragment=IndexFragment.newInstance("https://xiaoyou66.com/%E6%9D%BF%E5%9D%97/");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,indexfragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_notifications:
                    personFragment=new PersonFragment();
                    setTitle("个人信息");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,personFragment).commitAllowingStateLoss();
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        indexfragment=IndexFragment.newInstance("https://xiaoyou66.com/");
        getSupportFragmentManager().beginTransaction().replace(R.id.fr_container,indexfragment).commitAllowingStateLoss();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
    //这里我们重写fragment里面的方法
    @Override
    public void showwebdata(String s) {
        setTitle(s);//我们要修改自己的标题
    }
    //    这一段是重写返回方法
    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments) {
            /*如果是自己封装的Fragment的子类  判断是否需要处理返回事件*/
            if (fragment instanceof com.example.xiaoyouweb.fragment.IndexFragment) {
                if (((com.example.xiaoyouweb.fragment.IndexFragment) fragment).onBackPressed()) {
                    /*在Fragment中处理返回事件*/
                    return;
                }
            }
        }
        super.onBackPressed();
    }
    //动态申请权限代码
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
