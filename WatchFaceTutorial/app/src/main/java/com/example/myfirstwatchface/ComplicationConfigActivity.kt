/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myfirstwatchface

import android.R
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderChooserIntent
import android.support.wearable.complications.ProviderInfoRetriever
import android.support.wearable.complications.ProviderInfoRetriever.OnProviderInfoReceivedCallback
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import java.util.concurrent.Executors

/**
 * The watch-side config activity for [ComplicationWatchFaceService], which allows for setting
 * the left and right complications of watch face.
 */
class ComplicationConfigActivity : Activity(), View.OnClickListener {
    /**
     * Used by associated watch face ([ComplicationWatchFaceService]) to let this
     * configuration Activity know which complication locations are supported, their ids, and
     * supported complication data types.
     */
    // TODO: Step 3, intro 1
    enum class ComplicationLocation {
        LEFT, RIGHT
    }

    private var mLeftComplicationId = 0
    private var mRightComplicationId = 0

    // Selected complication id by user.
    private var mSelectedComplicationId = 0

    // ComponentName used to identify a specific service that renders the watch face.
    private var mWatchFaceComponentName: ComponentName? = null

    // Required to retrieve complication data from watch face for preview.
    private var mProviderInfoRetriever: ProviderInfoRetriever? = null
    private var mLeftComplicationBackground: ImageView? = null
    private var mRightComplicationBackground: ImageView? = null
    private var mLeftComplication: ImageButton? = null
    private var mRightComplication: ImageButton? = null
    private var mDefaultAddComplicationDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_config)
        mDefaultAddComplicationDrawable = getDrawable(R.drawable.add_complication)

        // TODO: Step 3, initialize 1
        mSelectedComplicationId = -1
        var myWatchFace2 = MyWatchFace2()
        mLeftComplicationId = myWatchFace2.getComplicationId(ComplicationLocation.LEFT)
        mRightComplicationId = myWatchFace2.getComplicationId(ComplicationLocation.RIGHT)
        mWatchFaceComponentName = ComponentName(
            applicationContext,
            MyWatchFace2::class.java
        )

        // Sets up left complication preview.
        mLeftComplicationBackground =
            findViewById<View>(R.id.left_complication_background) as ImageView
        mLeftComplication = findViewById<View>(R.id.left_complication) as ImageButton
        mLeftComplication!!.setOnClickListener(this)

        // Sets default as "Add Complication" icon.
        mLeftComplication!!.setImageDrawable(mDefaultAddComplicationDrawable)
        mLeftComplicationBackground!!.visibility = View.INVISIBLE

        // Sets up right complication preview.
        mRightComplicationBackground =
            findViewById<View>(R.id.right_complication_background) as ImageView
        mRightComplication = findViewById<View>(R.id.right_complication) as ImageButton
        mRightComplication!!.setOnClickListener(this)

        // Sets default as "Add Complication" icon.
        mRightComplication!!.setImageDrawable(mDefaultAddComplicationDrawable)
        mRightComplicationBackground!!.visibility = View.INVISIBLE

        // TODO: Step 3, initialize 2
        mProviderInfoRetriever =
            ProviderInfoRetriever(applicationContext, Executors.newCachedThreadPool())
        mProviderInfoRetriever!!.init()
        retrieveInitialComplicationsData()
    }

    override fun onDestroy() {
        super.onDestroy()

        // TODO: Step 3, release
        mProviderInfoRetriever!!.release()
    }

    // TODO: Step 3, retrieve complication data
    fun retrieveInitialComplicationsData() {
        var myWatchFace2 = MyWatchFace2()
        val complicationIds: IntArray = myWatchFace2.getComplicationIds()
        mProviderInfoRetriever!!.retrieveProviderInfo(
            object : OnProviderInfoReceivedCallback() {
                override fun onProviderInfoReceived(
                    watchFaceComplicationId: Int,
                    complicationProviderInfo: ComplicationProviderInfo?
                ) {
                    Log.d(
                        TAG,
                        "onProviderInfoReceived: $complicationProviderInfo"
                    )
                    updateComplicationViews(watchFaceComplicationId, complicationProviderInfo)
                }
            },
            mWatchFaceComponentName,
            *complicationIds
        )
    }

    override fun onClick(view: View) {
        if (view == mLeftComplication) {
            Log.d(TAG, "Left Complication click()")
            launchComplicationHelperActivity(ComplicationLocation.LEFT)
        } else if (view == mRightComplication) {
            Log.d(TAG, "Right Complication click()")
            launchComplicationHelperActivity(ComplicationLocation.RIGHT)
        }
    }

    // Verifies the watch face supports the complication location, then launches the helper
    // class, so user can choose their complication data provider.
    // TODO: Step 3, launch data selector
    private fun launchComplicationHelperActivity(complicationLocation: ComplicationLocation) {
        var myWatchFace2 = MyWatchFace2()
        mSelectedComplicationId = myWatchFace2.getComplicationId(complicationLocation)
        if (mSelectedComplicationId >= 0) {
            val supportedTypes: IntArray = myWatchFace2.getSupportedComplicationTypes(
                    complicationLocation
                )
            startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                    applicationContext,
                    mWatchFaceComponentName,
                    mSelectedComplicationId,
                    *supportedTypes
                ),
                COMPLICATION_CONFIG_REQUEST_CODE
            )
        } else {
            Log.d(TAG, "Complication not supported by watch face.")
        }
    }

    fun updateComplicationViews(
        watchFaceComplicationId: Int, complicationProviderInfo: ComplicationProviderInfo?
    ) {
        Log.d(
            TAG,
            "updateComplicationViews(): id: $watchFaceComplicationId"
        )
        Log.d(
            TAG,
            "\tinfo: $complicationProviderInfo"
        )
        if (watchFaceComplicationId == mLeftComplicationId) {
            if (complicationProviderInfo != null) {
                mLeftComplication!!.setImageIcon(complicationProviderInfo.providerIcon)
                mLeftComplicationBackground!!.visibility = View.VISIBLE
            } else {
                mLeftComplication!!.setImageDrawable(mDefaultAddComplicationDrawable)
                mLeftComplicationBackground!!.visibility = View.INVISIBLE
            }
        } else if (watchFaceComplicationId == mRightComplicationId) {
            if (complicationProviderInfo != null) {
                mRightComplication!!.setImageIcon(complicationProviderInfo.providerIcon)
                mRightComplicationBackground!!.visibility = View.VISIBLE
            } else {
                mRightComplication!!.setImageDrawable(mDefaultAddComplicationDrawable)
                mRightComplicationBackground!!.visibility = View.INVISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        // TODO: Step 3, update views
        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == RESULT_OK) {

            // Retrieves information for selected Complication provider.
            val complicationProviderInfo: ComplicationProviderInfo? = data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO)
            Log.d(
                TAG,
                "Provider: $complicationProviderInfo"
            )
            if (mSelectedComplicationId >= 0) {
                updateComplicationViews(mSelectedComplicationId, complicationProviderInfo)
            }
        }
    }

    companion object {
        private const val TAG = "ConfigActivity"
        const val COMPLICATION_CONFIG_REQUEST_CODE = 1001
    }
}