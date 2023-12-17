package eu.havy.canteen.api;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Api {

    public Api(Handler handler) {
        this.handler = handler;
    }

    String server_api_url = "http://gargi.ddns.net:3000/api/"; //"https://canteen.havy.eu/api/"
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

    public enum Request {
        AUTHENTICATE_USER,
        GET_USER_INFO,
        GET_MENU,
        GET_USER_CREDIT,
        ADD_USER_CREDIT,
        GET_ORDER_HISTORY,
        GET_DISH,
        ORDER_DISH,
        GET_CANTEEN_INFO;

        public static Request getEnum(int i) {
            return Request.values()[i];
        }

        public static String toString(int i) {
            return Request.values()[i].name();
        }
    }

    private JSONObject request(Method method, String api_url, JSONObject body) {
        try {
            URL url = new URL(api_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toString());
            connection.setRequestProperty("Accept", "application/json");
            if (body != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.toString().getBytes());
            }
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (connection.getErrorStream() != null) {
                JSONObject error;
                try {
                    error = new JSONObject(new BufferedReader(new InputStreamReader(connection.getErrorStream())).lines().toArray()[0].toString());
                } catch (JSONException e) {
                    error = new JSONObject().put("message", "Invalid JSON response from server");
                }
                if (error.has("message")) {
                    return new JSONObject().put("responseCode", responseCode).put("message", error.getString("message"));
                }
            }

            Object[] strs = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().toArray();
            String str = strs.length == 0 ? "" : strs[0].toString();

            connection.disconnect();

            JSONObject response = str.length() == 0 ? new JSONObject() : new JSONObject(str);
            response.put("responseCode", responseCode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return new JSONObject().put("exception", e.getMessage());
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void authenticateUser(String email, String password) {
        executor.execute(() -> {
            //Background work here
            JSONObject body = buildBody(Map.of("email", email, "password", password));
            JSONObject response = request(Method.POST, server_api_url+"auth", body);

            //UI Thread work here
            //handler.post(() -> {})
            handler.obtainMessage(Request.AUTHENTICATE_USER.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void getUserInfo(String token) {
        executor.execute(() -> {
            //Background work here
            JSONObject response = request(Method.GET, server_api_url+"user?token="+token, null);

            //UI Thread work here
            //handler.post(() -> {})
            handler.obtainMessage(Request.GET_USER_INFO.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void getMenu(String token, String date) {
        executor.execute(() -> {
            //Background work here
            JSONObject response = request(Method.GET, server_api_url+"menu?token="+token+"&date="+date, null);

            //UI Thread work here
            //handler.post(() -> {});
            handler.obtainMessage(Request.GET_MENU.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void getDish(String token, int dishId){
        executor.execute(() -> {
            //Background work here
            JSONObject response = request(Method.GET, server_api_url+"dish?token="+token+"&dishId="+dishId, null);

            //UI Thread work here
            //handler.post(() -> {});
            handler.obtainMessage(Request.GET_DISH.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });

    }

    public void getOrderHistory(String token) {
        executor.execute(() -> {
            JSONObject response = request(Method.GET, server_api_url+"order/history?token="+token, null);
            handler.obtainMessage(Request.GET_ORDER_HISTORY.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void getUserCredit(String token) {
        executor.execute(() -> {
            //Background work here
            JSONObject response = request(Method.GET, server_api_url+"credit?token="+token, null);

            //UI Thread work here
            //handler.post(() -> {});
            handler.obtainMessage(Request.GET_USER_CREDIT.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void addUserCredit(String token, int amount) {
        executor.execute(() -> {
            //Background work here
            JSONObject body = buildBody(Map.of("token", token, "credit", amount));
            JSONObject response = request(Method.POST, server_api_url+"credit", body);

            //UI Thread work here
            //handler.post(() -> {});
            handler.obtainMessage(Request.ADD_USER_CREDIT.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void orderDish(String token, int dishId) {
        executor.execute(() -> {
            //Background work here
            JSONObject body = buildBody(Map.of("token", token, "dishId", dishId, "date","2023-11-13"));
            JSONObject response = request(Method.POST, server_api_url+"order/create", body);

            //UI Thread work here
            //handler.post(() -> {});
            handler.obtainMessage(Request.ORDER_DISH.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    public void getCanteenInfo(String token) {
        executor.execute(() -> {
            //Background work here
            JSONObject response = request(Method.GET, server_api_url+"canteen?token="+token, null);

            //UI Thread work here
            //handler.post(() -> {});
            handler.obtainMessage(Request.GET_CANTEEN_INFO.ordinal(), getResponseCode(response), 0, response).sendToTarget();
        });
    }

    private JSONObject buildBody(Map<String, Object> params) {
        JSONObject body = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                body.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return body;
    }

    private int getResponseCode(JSONObject response) {
        int responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
        try {
            return response.getInt("responseCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseCode;
    }
}
