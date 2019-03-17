package com.gitusers.network

import android.util.Log
import android.webkit.MimeTypeMap
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.JsonSyntaxException
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class VolleyNetworkRequest<T>(
    method: Int,
    url: String,
    private val mResponseListener: Response.Listener<T>,
    private val mErrorListener: Response.ErrorListener,
    private val mHeaderParams: Map<String, String>?,
    private val mBodyParams: Map<String, Any>?,
    private val mFileParams: Map<String, File>?
) : Request<T>(method, url, mErrorListener) {

    internal var mUrl: String? = null
    private val mBuilder = MultipartEntityBuilder.create()
    private val listener = Response.Listener<String> { }

    init {
        Log.d(
            TAG,
            "VolleyNetworkRequest() called with: method = [$method], url = [$url], responseListener = [$mResponseListener], errorListener = [$mErrorListener], headerParams = [$mHeaderParams], bodyParams = [$mBodyParams], fileParams = [$mFileParams]"
        )
        retryPolicy = DefaultRetryPolicy(
            DEFAULT_TIMEOUT_MS,
            DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        buildMultipartEntity()
    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return mHeaderParams ?: HashMap()
    }

    /**
     * mFileParams and FILE_PART_NAME size must be equal else it will throw error
     */
    private fun buildMultipartEntity() {
        if (null != mFileParams) {
            for ((key, value) in mFileParams) {
                mBuilder.addBinaryBody(key, value, ContentType.create(getMimeType(value.path)), value.name)
            }
        }

        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"))

        if (null != mBodyParams) {
            try {
                for ((key, value) in mBodyParams) {
                    //entity.addPart(postEntityModel.getName(), new StringBody(postEntityModel.getValue(), ContentType.TEXT_PLAIN));
                    if (value is String)
                        mBuilder.addTextBody(key, value + "")
                    else
                        mBuilder.addTextBody(key, value.toString() + "", ContentType.APPLICATION_JSON)
                }
            } catch (e: Exception) {
                Log.e(TAG, "buildMultipartEntity: =" + e.message)
            }

        }

    }


    override fun getBodyContentType(): String {
        return mBuilder.build().contentType.value
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        try {
            mBuilder.build().writeTo(bos)
        } catch (e: IOException) {
            // FirebaseCrash.report(e);
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.")
            e.printStackTrace()
        }

        return bos.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<T> {
        try {
            val json = java.lang.String(response.data, HttpHeaderParser.parseCharset(response.headers))
            Log.e(TAG, "parseNetworkResponse: =$json")
            return Response.success(
                json,
                HttpHeaderParser.parseCacheHeaders(response)
            ) as Response<T> // it will return String
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            return Response.error(ParseError(e))
        } catch (e: Exception) {
            return Response.error(ParseError(e))
        }

    }

    override fun deliverResponse(response: T) {
        mResponseListener.onResponse(response)
    }

    companion object {

        private val TAG = VolleyNetworkRequest::class.java.simpleName
        private val DEFAULT_TIMEOUT_MS = 20000
        private val DEFAULT_MAX_RETRIES = 0

        fun getMimeType(url: String): String? {
            val extension = url.substring(url.lastIndexOf("."))
            val mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap)
            Log.d(TAG, "getMimeType() returned: " + mimeType!!)
            return mimeType
        }
    }
}

