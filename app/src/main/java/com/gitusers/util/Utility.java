package com.gitusers.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class Utility {

    private static Gson gson = new GsonBuilder().create();

    public static void loadCircularImage(Context context, ImageView img, String imageToLoad, int placeholderRes) {
        GlideApp.
                with(context)
                .load(imageToLoad)
                .circleCrop()
                .error(ContextCompat.getDrawable(context, placeholderRes))
                .into(img);

    }

    public static <T> ArrayList<T> getObjectListFromJsonString(String jsonData, Class myclass) {
        return new ArrayList<>(Arrays.asList((T[]) gson.fromJson(jsonData, myclass)));
    }

    public static boolean isConnected(Context mContext) {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public interface API {
        String GIT_USERS = "https://api.github.com/users";
    }

    public interface PARAMS {
        String PAGINATION_PARAM = "since";
    }
    public interface EXTRA {
        String SELECTED_USERS = "selectedUsers";
    }


}
