package com.example.xrecyclerview;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.Calendar;

/*
 *  @项目名：  pakinglog2.0
 *  @包名：    com.odin.parkinglog.z_retrofit_rxandroid_databinding.listener
 *  @文件名:   PerfectClickListener
 *  @创建者:   odin
 *  @创建时间:  2017/2/16 16:49
 *  @描述：    TODO
 */

/**
 * 避免在1秒内出发多次点击
 * Created by yangcai on 2016/1/15.
 */
public abstract class PerfectClickListener
        implements OnClickListener
{
    public static final int  MIN_CLICK_DELAY_TIME = 1000;
    private             long lastClickTime        = 0;
    private             int  id                   = -1;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance()
                                   .getTimeInMillis();
        int  mId         = v.getId();
        if (id != mId) {
            id = mId;
            lastClickTime = currentTime;
            onNoDoubleClick(v);
            return;
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);
}
