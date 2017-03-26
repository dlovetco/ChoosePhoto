package com.example.choosephoto;

/**
 * Created by 马杭辉 on 2017/3/21.
 */

public interface HttpResponseCallBack {
    void response(String response);//处理服务器返回成功
    void error(Exception e);//处理异常
}
