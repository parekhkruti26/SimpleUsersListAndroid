package com.gitusers.network;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gitusers.util.Utility;

public class WebCallClass<T> {
    private static final String TAG = WebCallClass.class.getSimpleName();
    private static final WebCallClass singleton = new WebCallClass();

    private WebCallClass() {
    }

    public static WebCallClass getInstance() {
        return singleton;
    }

    public void callGetGitUsers(Context context, int nextId, final CommonResponseListener commonListener
            , final UsersResponseListener successListener) {

        Log.d(TAG, "callGetGitUsers() called with: context = [" + context + "], nextId = [" + nextId +
                "], commonListener = [" + commonListener + "], successListener = [" + successListener + "]");

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                commonListener.volleyError();
            }
        };

        Response.Listener responseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
//                    JSONArray jsonArray = new JSONArray(response.toString());

                    successListener.usersSuccessResponse(response.toString());
            }
        };

        String url = new StringBuilder(Utility.API.GIT_USERS).append("?").append(Utility.PARAMS.PAGINATION_PARAM)
                .append("=").append(String.valueOf(nextId)).toString();

        @SuppressWarnings("unchecked")
        VolleyNetworkRequest volleyNetworkRequest = new VolleyNetworkRequest(Request.Method.GET,
                url,
                responseListener,
                errorListener,
                null,
                null,
                null);
        Volley.getInstance(context).addToRequestQueue(volleyNetworkRequest, Utility.API.GIT_USERS);


    }
    //////////////////////////generic interface for common responses ends//////////////////////////

    //////////////////////////generic interface for common responses starts//////////////////////////
    public interface CommonResponseListener {
        //Can add more methods as and when needed according to specific use cases
        void volleyError();
    }

    //////////////////////////Static Pages call start//////////////////////////
    public interface UsersResponseListener {
        void usersSuccessResponse(String json);
    }

    //////////////////////////Static Pages call ends//////////////////////////

}
