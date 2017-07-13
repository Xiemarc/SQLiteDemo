package com.yinshua.sqlitedemo.db;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yinshua.sqlitedemo.R;
import com.yinshua.sqlitedemo.bean.User;
import com.yinshua.sqlitedemo.db.daoimpl.UserDao;
import com.yinshua.sqlitedemo.permission.PermissionFail;
import com.yinshua.sqlitedemo.permission.PermissionHelper;
import com.yinshua.sqlitedemo.permission.PermissionSuccess;

import java.util.List;

public class DbActivity extends AppCompatActivity {
    IBaseDao<User> baseDao;
    private static final int REQUEST_CODE_PERMISSION_OTHER = 0x0011;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionHelper.with(this).requestCode(REQUEST_CODE_PERMISSION_OTHER)
                .requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}).request();
    }

    @PermissionSuccess(requestCode = REQUEST_CODE_PERMISSION_OTHER)
    private void initBaseDao() {
        baseDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, User.class);
    }


    public void save(View v) {
        User user = new User("xie", "1231231");
        Long insert = baseDao.insert(user);
        if (insert != -1) {
            Toast.makeText(this, "插入成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void update(View v) {
        User where = new User();
        where.setName("xie");
        User user = new User("谢之强", "xie388212");
        int update = baseDao.update(user, where);
        if (update != 0) {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(View v) {
        User where = new User();
        where.setName("xie");
        int delete = baseDao.delete(where);
        if (delete != -1) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void query(View v) {
        User user = new User();
        user.setName("谢之强");
        List<User> query = baseDao.query(user);
        for (User user1 : query) {
            Log.i("marc", user1.getPassword());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionResult(this, requestCode, permissions);
    }


    @PermissionFail(requestCode = REQUEST_CODE_PERMISSION_OTHER)
    public void showFail() {
        Toast.makeText(this, "没有给权限的噻", Toast.LENGTH_SHORT).show();
//        System.exit(0);
    }


}
