package com.pointim.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pointim.R;
import com.pointim.ui.ZoomImageView;

public class ShowImageActivity extends AppCompatActivity {
    private ZoomImageView image;
    private String image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        image_path = getIntent().getStringExtra("image_path");
        initView();
    }

    private void initView() {
        image = (ZoomImageView) findViewById(R.id.image);
        Bitmap bm = BitmapFactory.decodeFile(image_path);// 通过文件路径获取bitmap
        image.setImage(bm);//设置显示的图片
    }
}
