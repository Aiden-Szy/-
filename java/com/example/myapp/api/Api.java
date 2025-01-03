package com.example.myapp.api;

import android.util.Log;

import com.example.myapp.util.AppConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Api 类用于封装网络请求逻辑，提供静态方法来配置请求参数和执行 POST 请求。
 */
public class Api {
    private static OkHttpClient client;// 静态 OkHttpClient 实例，用于发送 HTTP 请求。
    private static String requestUrl;//请求的目标 URL。
    private static HashMap<String,Object> mParams;//请求参数的 HashMap，存储键值对形式的参数。
    public static Api api = new Api();//单例模式下的 Api 实例，确保全局只有一个 Api 对象

    /**
     * 构造函数，默认私有以支持单例模式。
     */
    public Api(){

    }

    /**
     * 配置 API 请求的基本信息，包括目标 URL 和请求参数。
     *
     * @param url    目标服务器的 URL 地址
     * @param params 包含请求参数的 HashMap，键为参数名，值为参数值
     * @return 返回当前 Api 实例，方便链式调用
     */
    public static Api config(String url,HashMap<String,Object> params){
        client = new OkHttpClient.Builder()
                .build();
        requestUrl = url;
        mParams = params;
        return api;
    }

    /**
     * 发送 POST 请求，并通过回调接口处理响应结果。
     *
     * @param callback 用于处理请求完成后的回调，定义了成功和失败两种情况
     */
    public void postRequet(TtitCallback callback){
        // 注意：TtitCallback 应该是一个自定义接口，用来接收请求的结果
        JSONObject jsonObject = new JSONObject(mParams);
        String jsonStr = jsonObject.toString();
        RequestBody requestBodyJson =
                RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                        , jsonStr);
        //第三步创建Rquest
        Request request = new Request.Builder()
                .url(AppConfig.BASE_URL + "/app/login")
                .addHeader("contentType", "application/json;charset=UTF-8")
                .post(requestBodyJson)
                .build();
        //第四步创建call回调对象
        final Call call = client.newCall(request);
        //第五步发起请求
        call.enqueue(new Callback() {// 确保使用的是 okhttp3 的 Callback
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", e.getMessage());
                runOnUiThread(() -> showToast("网络请求失败，请检查网络连接"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    runOnUiThread(() -> showToast(result));
                } else {
                    Log.e("OkHttp", "Request failed with code: " + response.code());
                    runOnUiThread(() -> showToast("请求失败，状态码：" + response.code()));
                }
            }
        });
    }
}
