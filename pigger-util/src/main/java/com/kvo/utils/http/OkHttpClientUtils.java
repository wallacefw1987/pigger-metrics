package com.kvo.utils.http;

import com.alibaba.fastjson.JSON;
import com.kvo.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class OkHttpClientUtils {

    public static final MediaType JSON_TYPE_UTF8 = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType JSON_TYPE = MediaType.parse("application/json");

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY_NOT_HEAD))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * 以“application/json” post提交数据，提交对象为object
     *
     * @param url
     * @param object
     * @return
     */
    public static String postReturnBody(@NotNull String url, Object object) throws IOException {
        return post(url, object).body().string();
    }

    /**
     * okhttpclient通过formbody，post提交
     *
     * @param url
     * @param body
     * @return
     */
    public static String postReturnBody(@NotNull String url, FormBody body) throws IOException {
        return post(url, body).body().string();
    }

    /**
     * okhttpclient以“application/json” post提交数据
     *
     * @param url
     * @param object
     * @return Response
     * @throws IOException
     */
    public static Response post(@NotNull String url, Object object) throws IOException {
        String json = JSON.toJSONString(object);
        RequestBody body = RequestBody.create(JSON_TYPE_UTF8, json);
        return post(url, body, null);
    }


    /**
     * okhttpclient通过formbody，post提交,返回Response
     *
     * @param url
     * @param body
     * @return Response
     * @throws IOException
     */
    public static Response post(@NotNull String url, FormBody body) throws IOException {
        return post(url, body, null);
    }

    /**
     * OKhttpClient post提交方法
     *
     * @param url
     * @param body
     * @param headers
     * @return
     * @throws IOException
     */
    public static Response post(@NotNull String url, RequestBody body, Headers headers) throws IOException {
        //这里获取url数据
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();

        Request.Builder builder = new Request.Builder().url(httpUrlBuilder.build());
        if (Objects.nonNull(headers)) {
            builder.headers(headers);
        } else {
            log.debug("post body is null url<{}>", url);
        }
        if (Objects.nonNull(body)) {
            builder.post(body);
        }
        //这里RequestBody已经包含了MediaType

        return client.newCall(builder.build()).execute();
    }

    /**
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static Response get(@NotNull String url, Map<String, String> params) throws IOException {
        return get(url, params, null);
    }

    /**
     * okhttpclient 通过url 和 params 合拼,get提交方法
     *
     * @param url
     * @param params 参数集
     * @return
     * @throws IOException
     */
    public static String getReturnBody(@NotNull String url, Map<String, String> params) throws IOException {
        return get(url, params).body().string();
    }

    /**
     * okhttpclient通过url，get提交
     *
     * @param url 传送url和参数集
     * @return
     */
    public static String getReturnBody(@NotNull String url) throws IOException {
        return get(url, null, null).body().string();
    }

    /**
     * okhttpclient通过url，get提交，返回Response对象
     *
     * @param url
     * @return Response
     * @throws IOException
     */
    public static Response get(@NotNull String url) throws IOException {
        return get(url, null, null);
    }

    /**
     * okhttpclient通过设置Header与url，Get方法提交
     *
     * @param url
     * @param headers 请求头
     * @return
     */
    public static String gerReturnBody(@NotNull String url, Headers headers) throws IOException {
        return get(url, null, headers).body().string();
    }

    /**
     * okhttpclient通过设置url与header,get提交，返回Response对象
     *
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    public static Response get(@NotNull String url, Headers headers) throws IOException {
        return get(url, null, headers);
    }

    /**
     * okhttpclient通过设置url、params、Header，Get方法提交
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws IOException
     */
    public static Response get(@NotNull String url, Map<String, String> params, Headers headers) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("get params is null or empty url<{}>", url);
            }
        }
        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());
        if (headers != null) {
            requestBuilder.headers(headers);
        }
        return client.newCall(requestBuilder.build()).execute();
    }

    /**
     * okhttpclient 异步post提交object对象
     *
     * @param url
     * @param object
     * @param callback 如果Callback为空则默认只打日志
     * @return Call
     */
    public static Call asyncPost(@NotNull String url, Object object, Callback callback) throws Exception {
        String json = JSON.toJSONString(object);
        return asyncPost(url, json, callback);
    }


    /**
     * okhttpclient 异步post提交json对象
     *
     * @param url
     * @param json
     * @param callback 如果Callback为空则默认只打日志
     * @return
     */
    public static Call asyncPost(@NotNull String url, String json, Callback callback) throws Exception {
        RequestBody body = RequestBody.create(JSON_TYPE_UTF8, json);
        return asyncPost(url, body, null, callback);
    }

    /**
     * okhttpclient 异步post提交object对象
     *
     * @param url
     * @param object
     * @param headers
     * @param callback
     * @return
     * @throws Exception
     */
    public static Call asyncPost(@NotNull String url, Object object, Headers headers, Callback callback) {
        String json = JSON.toJSONString(object);
        return asyncPost(url, json, headers, callback);
    }

    /**
     * okhttpclient 异步post提交json对象
     *
     * @param url
     * @param json
     * @param headers
     * @param callback
     * @return
     * @throws Exception
     */
    public static Call asyncPost(@NotNull String url, String json, Headers headers, Callback callback) {
        RequestBody body = RequestBody.create(JSON_TYPE_UTF8, json);
        return asyncPost(url, body, headers, callback);
    }

    /**
     * okhttpclient 异步post提交formbody对象
     *
     * @param url
     * @param body
     * @param callback 如果Callback为空则默认只打日志
     * @return
     */
    public static Call asyncPost(@NotNull String url, RequestBody body, Headers headers, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        Request.Builder builder = new Request.Builder().url(urlBuilder.build());
        if (Objects.nonNull(body)) {
            builder.post(body);
        }
        if (Objects.nonNull(headers)) {
            builder.headers(headers);
        }
        Call call = client.newCall(builder.build());
        if (callback == null) {
            callback = new LogCallBack();
        }
        call.enqueue(callback);
        log.info("async request! url<{}>, bodyM<{}>", url, body);
        return call;
    }

    /**
     * 默认日志记录回调类
     */
    @Slf4j
    public static class LogCallBack implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            log.error(String.format("url<%s> params<%s>", call.request().url(), call.request().body()), e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream is = response.body().byteStream();
            String result = IOUtils.toString(is, StandardCharsets.UTF_8);
            log.info(String.format("url<%s> params<%s> response<%s>", call.request().url(), call.request().body(), result));
        }
    }
}