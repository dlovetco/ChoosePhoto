package com.example.choosephoto;

import org.json.JSONObject;


import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkhttpUtils {

    //定义一个JSON的MediaType（互联网媒体类型）
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //1.定义一个OkhttpClient
    private static OkHttpClient client = new OkHttpClient();

    public static void doPost(final String url, final File file, JSONObject jsonObject, final HttpResponseCallBack httpResponseCallBack)
    {
        //建立body
        RequestBody body = RequestBody.create(JSON,jsonObject.toString());

        //建立请求
        Request request = new Request.Builder().post(body).url(url).build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpResponseCallBack.error(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                httpResponseCallBack.response(s);
                if (file!=null)//如果有文件需要传输的话
                {
                    doPostPicture(url, file, new HttpResponseCallBack() {
                        @Override
                        public void response(String response) {
                            //做操作
                        }

                        @Override
                        public void error(Exception e) {
                            //做操作
                        }
                    });
                }
            }
        });
    }
    //参数为要上传的网址，本地照片在本地的地址，我们自己定义的接口
    private static void doPostPicture(String url, File file,final HttpResponseCallBack httpResponseCallBack) {

        //2.创建一个请求体
        RequestBody body;
        //3.创建一个请求体建造器
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addFormDataPart("headPicture", "headPicture.jpg", RequestBody.create(MediaType.parse("image/jpg"), file)).build();

        body = builder.build();

        //3.创建一个请求，利用构建器方式添加url和请求体。
        Request request = new Request.Builder().post(body).url(url).build();

        //4.定义一个call，利用okhttpclient的newcall方法来创建对象。因为Call是一个接口不能利用构造器实例化。
        Call call = client.newCall(request);

        //5.这是异步调度方法，上传和接受的工作都在子线程里面运作，如果要使用同步的方法就用call.excute(),此方法返回的就是Response
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpResponseCallBack.error(e);//错误发生时的处理
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpResponseCallBack.response(response.body().string());//把服务器发回来的数据response解析成string传入方法
            }
        });
    }
}
