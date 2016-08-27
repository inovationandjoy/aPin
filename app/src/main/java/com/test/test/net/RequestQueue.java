/*
 *
 * # Copyright 2016 Intellisis Inc.  All rights reserved.
 * #
 * # Use of this source code is governed by a BSD-style
 * # license that can be found in the LICENSE file
 */

package com.test.test.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.test.test.util.RequestHelper;

/**
 * Created by ndarade on 4/29/16.
 */
public class RequestQueue {
    private static final String TAG = "KnulrdRequests";
    private static RequestQueue singletonRequestQueue = null;
    private com.android.volley.RequestQueue mRequestQueue;
    private static Context mCtx;

    public static RequestQueue getInstance(Context context) {
        if (singletonRequestQueue == null) {
            singletonRequestQueue = new RequestQueue(context);
        }
        return singletonRequestQueue;
    }

    private RequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public com.android.volley.RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.addMarker(TAG);
        getRequestQueue().add(req);
    }

    public void cancleAllRequest() {
        getRequestQueue().cancelAll(TAG);
    }


    public static void submit(Context context, CustomRequest jsObjRequest, boolean checkLogin) {
        if (!RequestHelper.isUserLoggedIn() && checkLogin) {
            RequestHelper.Login(context, jsObjRequest);
        } else {
            RequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
        }
    }

    public static void submitMultipartRequest(Context context, MultipartRequest multipartRequest, boolean checkLogin) {
        RequestQueue.getInstance(context).addToRequestQueue(multipartRequest);
    }

    public static void submitAdminLogin(Context context, AdminLoginRequest adminLoginRequest) {
        RequestQueue.getInstance(context).addToRequestQueue(adminLoginRequest);

    }
}
