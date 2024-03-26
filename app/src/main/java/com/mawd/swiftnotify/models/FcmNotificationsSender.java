package com.mawd.swiftnotify.models;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mawd.swiftnotify.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {
    String userFcmToken;
    String body;
    Context mContext;
    Activity mActivity;
    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAAQvaEU90:APA91bHuRd-D3vZ9Ta-LdQdGXRYEg9A10Jc215bqjsake5E0jA5aHzeUW0NLmFCqCzOPxtfl05CkMz8Mb7igIvJbY2z8f8kAk5iXzkbTEseccMCaExw6asZd1HlhIg5wi4GWsCoVjd4t";

    public FcmNotificationsSender(String userFcmToken, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }
    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notifyObject = new JSONObject();
            notifyObject.put("title", "Swift Notify");
            notifyObject.put("body", body);
            notifyObject.put("icon", R.drawable.ic_notification);
            mainObj.put("notification", notifyObject);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                // code run is got response
            }, error -> {
                // code run is got error
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
