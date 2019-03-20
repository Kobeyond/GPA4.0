package com.example.gpa_plus;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2018/8/24.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    //添加一个新活动
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    //移除一个活动
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    //关闭所有活动
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}