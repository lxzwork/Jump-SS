package com.example.dell.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell.vo.ShowVO;
import com.zxing.support.library.qrcode.QRCodeEncode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class DetailsActivity extends AppCompatActivity {
    //常量
    public static final String DE_ADDRESS = "0";
    public static final String DE_IP = "1";
    public static final String DE_POST = "2";
    public static final String DE_PASSWORD = "3";
    public static final String DE_ENCRYPTION = "4";
    public static final String DE_NAME = "5";
    public static final String DE_IMG = "6";
    public static final String DE_COUNTRYIMAGE = "7";
    private ShowVO mShowVO;
    private ImageView mImageCountry;
    private ImageView mImageQR;
    private Toolbar mToolbar;
    private TextView mTextViewIP, mTextViewPost, mTextViewPassword, mTextViewJm;
    private CollapsingToolbarLayout mCollBar;
    //二维码
    private Bitmap mBitmapCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        bindID();
        //设置toolbar
        setSupportActionBar(mToolbar);
        //设置返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //获取前面的数据
        getData();
        //设置数据
        setData();
        //长按提示
        mImageQR.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, "下载图片？", Snackbar.LENGTH_LONG)
                        .setAction("确认", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //下载图片
                                downloadPic();
                            }
                        }).show();
                return true;
            }
        });

    }

    /**
     * 下载二维码
     */
    private void downloadPic() {
        if (mShowVO.getmIp().length() == 0) {
            //没有流量
            Toast.makeText(DetailsActivity.this, "流量耗尽，请更换节点", Toast.LENGTH_SHORT).show();
            return;
        }
        //有流量
        if (mBitmapCode != null) {

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), mShowVO.getmAddress() + ":" + mShowVO.getmIp() + ".png");
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                mBitmapCode.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
                Toast.makeText(DetailsActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }
    }


    //菜单的返回按钮

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 设置数据
     */
    private void setData() {
        //国家
        Glide.with(DetailsActivity.this).load(mShowVO.getmCountry()).into(mImageCountry);
        //二维码
        Log.e("pic", mShowVO.getmImg());
        //加载二维码
        mBitmapCode = loadQR(mShowVO.getmImg());
        mImageQR.setImageBitmap(mBitmapCode);
        //标题栏
        mCollBar.setTitle(mShowVO.getmAddress());
        //ip
        mTextViewIP.setText(mShowVO.getmIp());
        //端口
        mTextViewPost.setText(mShowVO.getmPost());
        //密码
        mTextViewPassword.setText(mShowVO.getmPassword());
        //加密
        mTextViewJm.setText(mShowVO.getmEncryption());
        //判断是否接近耗尽
        if (mTextViewIP.getText().length() == 0) {
            //ip
            mTextViewIP.setText("流量耗尽");
        }
    }

    /**
     * 加载二维码
     *
     * @param strUrl
     */
    private Bitmap loadQR(String strUrl) {
        QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
        builder.setBackgroundColor(0xffffff)
                .setOutputBitmapHeight(1000)
                .setOutputBitmapWidth(1000)
                .setOutputBitmapPadding(1);
        Bitmap bitmap = builder.build().encode(strUrl);
        return bitmap;
    }

    /**
     * 绑定id
     */
    private void bindID() {
        mImageCountry = (ImageView) findViewById(R.id.details_imageview_country);
        mImageQR = (ImageView) findViewById(R.id.details_imageview_qr);
        mToolbar = (Toolbar) findViewById(R.id.details_toolbar);
        mTextViewIP = (TextView) findViewById(R.id.details_textView_ip);
        mTextViewPost = (TextView) findViewById(R.id.details_textView_post);
        mTextViewPassword = (TextView) findViewById(R.id.details_textView_password);
        mTextViewJm = (TextView) findViewById(R.id.details_textView_jm);
        mCollBar = (CollapsingToolbarLayout) findViewById(R.id.details_collapsingToolbarLayout);
    }

    /**
     * 获取数据
     */
    private void getData() {
        Intent intent = getIntent();
        String mAddress = intent.getStringExtra(DE_ADDRESS);
        String mIp = intent.getStringExtra(DE_IP);
        String mPost = intent.getStringExtra(DE_POST);
        String mPassword = intent.getStringExtra(DE_PASSWORD);
        String mEncryption = intent.getStringExtra(DE_ENCRYPTION);
        String mName = intent.getStringExtra(DE_NAME);
        String mImg = intent.getStringExtra(DE_IMG);
        int mCountry = intent.getIntExtra(DE_COUNTRYIMAGE, R.mipmap.ic_launcher);
        mShowVO = new ShowVO(mAddress, mIp, mPost, mPassword, mEncryption, mName, mImg, mCountry);
    }


}
