package eu.havy.canteen.api;

import android.os.Bundle;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Api {

    public Api(Handler handler) {
        this.handler = handler;
    }

    //String api_url = "https://canteen.havy.eu/api/"; //https://www.stud.fit.vutbr.cz/~xvolfr00
    String server_api_url = "http://gargi.ddns.net:3000/api/";
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler;

    private enum Method {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private final String method;

        Method(String method) {
            this.method = method;
        }

        public String toString() {
            return this.method;
        }
    }

    private String request(Method method, String api_url, String body) {
        try {
            URL url = new URL(api_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toString());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder str = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }

                reader.close();
                connection.disconnect();

                return str.toString();
            } else {
                return "HTTP request failed with response code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }

    public void authenticateUser(String email, String password) {
        executor.execute(() -> {
            //Background work here
            String response = request(Method.POST, server_api_url+"auth", "{\"email\": \""+email+"\", \"password\": \""+password+"\"}");

            //UI Thread work here
            //handler.post(() -> {});
            Bundle bundle = new Bundle();
            bundle.putString("response", response);
            handler.obtainMessage(200, bundle).sendToTarget();
        });
    }

    public void getUserInfo(String token) {
        executor.execute(() -> {
            //Background work here
            String response = request(Method.GET, server_api_url+"user", "{\"token\": \""+token+"\"}");

            //UI Thread work here
            //handler.post(() -> {});
            Bundle bundle = new Bundle();
            bundle.putString("response", response);
            handler.obtainMessage(200, bundle).sendToTarget();
        });
    }

    public void getDishes() {
        executor.execute(() -> {
            //Background work here
            String response = request(Method.GET, server_api_url+"menu", "{\"userId\": 1,\"date\": \"2023-11-13\"}");

            //UI Thread work here
            //handler.post(() -> {});
            Bundle bundle = new Bundle();
            bundle.putString("response", response);
            handler.obtainMessage(200, bundle).sendToTarget();
        });
    }
    public void addCredits(String amount) {
        executor.execute(() -> {
            //Background work here
            String response = request(Method.POST, server_api_url+"credit", "{\"userId\": 1,\"amount\":" + amount + " }");

            //UI Thread work here
            //handler.post(() -> {});
            Bundle bundle = new Bundle();
            bundle.putString("response", response);
            handler.obtainMessage(200, bundle).sendToTarget();
        });
    }

}
