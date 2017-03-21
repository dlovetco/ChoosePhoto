package com.example.choosephoto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView headPicture;
    private Uri imageUri;//图片存储的路径
    private File file;//需要存储的图片文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        headPicture = (ImageView) findViewById(R.id.headPicture);
        file = new File(Environment.getExternalStorageDirectory(), "headPicture.jpg");//新建一个文件（路径，文件名称）
        //Environment.getExternalStorageDirectory()为获取sd的根目录。
        imageUri = Uri.fromFile(file);
    }

    public void choosePicture(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择您的头像方式");
        String[] string = {"拍照", "从相册里选择"};
        builder.setItems(string, new DialogInterface.OnClickListener() {//(实现了charsequence的类数组这里用string数组，一个点击事件的监听器)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        //拍照
                        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//打开系统照相机
                        openCamera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        startActivityForResult(openCamera,0);
                        break;
                    case 1:
                        //从相册里选择
                        Intent chooseInAlbum = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(chooseInAlbum,1);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                //拍照
                cropPicture(imageUri);
                break;
            case 1:
                //从相册获取
                if (resultCode==RESULT_OK) {
                   cropPicture(data.getData());
                }
                break;
            case 2:
                //裁剪之后
                if (resultCode == RESULT_OK) {
                    setHeadPicture();
                }
                break;
        }
    }

    private void setHeadPicture()
    {
        try {
            //根据imageUri用getContentResolver来获取流对象 再转化成bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            if (bitmap==null)
            {
                //判断bitmap是否为空
                Toast.makeText(this,"图像没有存储到sd卡根目录",Toast.LENGTH_SHORT).show();
            }
            headPicture.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {

        }
    }

    private void cropPicture(Uri uri)
    {
        //新建一个表示裁剪的Intent
        Intent intent = new Intent("com.android.camera.action.CROP");
        //表明我要裁剪的目标是uri这个地址，文件类型是图片
        intent.setDataAndType(uri,"image/*");
        //指定长宽的比例为1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //指定宽高为1000
        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 1000);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent,2);
    }
}
