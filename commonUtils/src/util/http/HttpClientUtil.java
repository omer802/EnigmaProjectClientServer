package util.http;
import okhttp3.*;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    static
    {
         HTTP_CLIENT =
                new OkHttpClient.Builder()
                        .cookieJar(simpleCookieManager)
                        .followRedirects(false)
                        .build();
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    }
    private final static OkHttpClient HTTP_CLIENT;



    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        runAsync(request, callback);
    }
    public static OkHttpClient getHttpClient(){
        return HTTP_CLIENT;
    }
    public static Response runSync(Request request) throws IOException {
        Call call = HTTP_CLIENT.newCall(request);

        Response response = call.execute();
        return response;
    }
    public static void runAsync(Request request, Callback callback){
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }
    public static void runAsyncPost(String finalUrl,RequestBody body, Callback callback){
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        runAsync(request, callback);
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
