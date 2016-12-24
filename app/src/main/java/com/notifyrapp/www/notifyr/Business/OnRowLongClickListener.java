package com.notifyrapp.www.notifyr.Business;

import android.content.Context;
import android.view.View;

public class OnRowLongClickListener implements View.OnLongClickListener
{
    private Context mContext;
    private Object mObj;
    public OnRowLongClickListener(Context context, Object obj) {
        this.mObj = obj;
        this.mContext  = context;
    }


    @Override
    public boolean onLongClick(View v)
    {

        return false;
    }

}
