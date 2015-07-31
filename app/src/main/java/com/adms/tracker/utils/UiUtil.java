package com.adms.tracker.utils;

import android.util.Log;
import android.view.View;

/**
 * Created by admin on 07/07/15.
 */
public class UiUtil {
    private static final String TAG = UiUtil.class.getCanonicalName();
   // private static final Logger logger = new Logger(UiUtil.class);

    public static void animateLayouts(View view){
        if (view == null) {
            Log.d(TAG, "null view cannot be animated!");
            return;
        }
        LayoutAnimationControllerUtil messageController;
        messageController = new LayoutAnimationControllerUtil(view);
        messageController.showMessageBar();
    }




}
