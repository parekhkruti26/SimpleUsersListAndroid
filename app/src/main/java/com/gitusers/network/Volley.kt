package com.gitusers.network

import android.content.Context
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack

class Volley private constructor(context: Context) {
    var requestQueue: RequestQueue? = null
        private set

    init {
        if (requestQueue == null) {
            val cache = DiskBasedCache(context.cacheDir, 10 * 1024 * 1024)
            val network = BasicNetwork(HurlStack())

            //3rd parameter is number of network dispatcher threads to create
            requestQueue = RequestQueue(cache, network, 10)

            requestQueue!!.start()
        }
    }

    fun addToRequestQueue(request: Request<*>, tag: String) {
        request.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueue?.add(request)
    }

    companion object {

        private var mInstance: Volley? = null
        private val TAG = Volley::class.java.simpleName

        @JvmStatic
        fun getInstance(context: Context): Volley {
            return mInstance ?: synchronized(this) {
                Volley(context).also {
                    mInstance = it
                }
            }
        }
    }

}
