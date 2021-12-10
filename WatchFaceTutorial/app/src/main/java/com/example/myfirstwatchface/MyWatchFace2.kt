package com.example.myfirstwatchface

import android.app.PendingIntent.CanceledException
import android.content.*
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationHelperActivity
import android.support.wearable.complications.rendering.ComplicationDrawable
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.myfirstwatchface.ComplicationConfigActivity.ComplicationLocation
import java.lang.ref.WeakReference
import java.util.*


/**
 * Digital watch face with seconds. In ambient mode, the seconds aren"t displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
class MyWatchFace2 : CanvasWatchFaceService() {

    //1
    private val LEFT_COMPLICATION_ID = 0
    private val RIGHT_COMPLICATION_ID = 1

    private val COMPLICATION_IDS = intArrayOf(LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID)

    private val COMPLICATION_SUPPORTED_TYPES = arrayOf(
        intArrayOf(
            ComplicationData.TYPE_RANGED_VALUE,
            ComplicationData.TYPE_ICON,
            ComplicationData.TYPE_SHORT_TEXT,
            ComplicationData.TYPE_SMALL_IMAGE
        ), intArrayOf(
            ComplicationData.TYPE_RANGED_VALUE,
            ComplicationData.TYPE_ICON,
            ComplicationData.TYPE_SHORT_TEXT,
            ComplicationData.TYPE_SMALL_IMAGE
        )
    )

    fun getComplicationId(
        complicationLocation: ComplicationLocation?
    ): Int {
        // Add any other supported locations here you would like to support. In our case, we are
        // only supporting a left and right complication.
        return when (complicationLocation) {
            ComplicationLocation.LEFT -> LEFT_COMPLICATION_ID
            ComplicationLocation.RIGHT -> RIGHT_COMPLICATION_ID
            else -> -1
        }
    }

    fun getComplicationIds(): IntArray {
        return COMPLICATION_IDS
    }

    fun getSupportedComplicationTypes(
        complicationLocation: ComplicationLocation?
    ): IntArray {
        // Add any other supported locations here.
        return when (complicationLocation) {
            ComplicationLocation.LEFT -> COMPLICATION_SUPPORTED_TYPES[0]
            ComplicationLocation.RIGHT -> COMPLICATION_SUPPORTED_TYPES[1]
            else -> intArrayOf()
        }
    }
    //1

    companion object {
        /**
         * Updates rate in milliseconds for interactive mode. We update once a second since seconds
         * are displayed in interactive mode.
         */
        private const val INTERACTIVE_UPDATE_RATE_MS = 1000

        /**
         * Handler message id for updating the time periodically in interactive mode.
         */
        private const val MSG_UPDATE_TIME = 0
    }

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: MyWatchFace2.Engine) : Handler() {
        private val mWeakReference: WeakReference<MyWatchFace2.Engine> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private lateinit var mCalendar: Calendar

        private var mRegisteredTimeZoneReceiver = false

        private var mXOffset: Float = 0F
        private var mYOffset: Float = 0F

        private lateinit var mBackgroundPaint: Paint
        private lateinit var mTextPaint: Paint
        private lateinit var mBackgroundBitmap: Bitmap

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private var mLowBitAmbient: Boolean = false
        private var mBurnInProtection: Boolean = false
        private var mAmbient: Boolean = false

        //1.1
        private var mActiveComplicationDataSparseArray: SparseArray<ComplicationData>? = null

        private var mComplicationDrawableSparseArray: SparseArray<ComplicationDrawable>? = null
        //1.1

        private val mUpdateTimeHandler: Handler = EngineHandler(this)

        private val mTimeZoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            val NORMAL_TYPEFACE = Typeface.createFromAsset(getBaseContext().getAssets(), "font/blockgame.ttf")

            setWatchFaceStyle(
                WatchFaceStyle.Builder(this@MyWatchFace2)
                    .setAcceptsTapEvents(true)
                    .build()
            )

            //3
            initializeComplications()
            //3

            mCalendar = Calendar.getInstance()

            val resources = this@MyWatchFace2.resources
            mYOffset = resources.getDimension(R.dimen.digital_y_offset)

            // Initializes background.
            mBackgroundPaint = Paint().apply {
                color = ContextCompat.getColor(applicationContext, R.color.background)
            }

            mBackgroundBitmap =
                BitmapFactory.decodeResource(resources, R.drawable.windows)


            // Initializes Watch Face.
            mTextPaint = Paint().apply {
                typeface = NORMAL_TYPEFACE
                isAntiAlias = true
                color = Color.BLACK
                //setShadowLayer(6f, 2f, 2f, Color.rgb(255, 186, 1))
                textAlign = Paint.Align.CENTER

            }
        }

        //2
        private fun initializeComplications() {
            Log.d("Complication:", "initializeComplications() ran")
            mActiveComplicationDataSparseArray = SparseArray(COMPLICATION_IDS.size)
            val leftComplicationDrawable =
                getDrawable(R.drawable.custom_complication_styles) as ComplicationDrawable?
            leftComplicationDrawable!!.setContext(applicationContext)
            val rightComplicationDrawable =
                getDrawable(R.drawable.custom_complication_styles) as ComplicationDrawable?
            rightComplicationDrawable!!.setContext(applicationContext)
            mComplicationDrawableSparseArray = SparseArray(COMPLICATION_IDS.size)
            mComplicationDrawableSparseArray!!.put(LEFT_COMPLICATION_ID, leftComplicationDrawable)
            mComplicationDrawableSparseArray!!.put(RIGHT_COMPLICATION_ID, rightComplicationDrawable)
            setActiveComplications(LEFT_COMPLICATION_ID)
        }

        override fun onComplicationDataUpdate(
            complicationId: Int, complicationData: ComplicationData?
        ) {
            Log.d("Complications!: ", "onComplicationDataUpdate() id: $complicationId"
            )

            // Adds/updates active complication data in the array.
            mActiveComplicationDataSparseArray!!.put(complicationId, complicationData)

            // Updates correct ComplicationDrawable with updated data.
            val complicationDrawable = mComplicationDrawableSparseArray!![complicationId]
            complicationDrawable.setComplicationData(complicationData)
            invalidate()
        }
        //2

        override fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }


        @RequiresApi(Build.VERSION_CODES.S)
        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            mLowBitAmbient = properties.getBoolean(
                WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false
            )
            mBurnInProtection = properties.getBoolean(
                WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false
            )

            // Updates complications to properly render in ambient mode based on the
            // screen's capabilities.

            // Updates complications to properly render in ambient mode based on the
            // screen's capabilities.
            var complicationDrawable: ComplicationDrawable?

            for (i in COMPLICATION_IDS.indices) {
                complicationDrawable =
                    mComplicationDrawableSparseArray!![COMPLICATION_IDS[i]]
                if (complicationDrawable != null) {
                    complicationDrawable.setLowBitAmbient(mLowBitAmbient)
                    complicationDrawable.setBurnInProtection(mBurnInProtection)
                }
            }
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            mAmbient = inAmbientMode

            if (mLowBitAmbient) {
                mTextPaint.isAntiAlias = !inAmbientMode
            }

            //updateWatchHandStyles()

            var complicationDrawable: ComplicationDrawable

            for (i in COMPLICATION_IDS.indices) {
                complicationDrawable =
                    mComplicationDrawableSparseArray!![COMPLICATION_IDS[i]]
                complicationDrawable.setInAmbientMode(mAmbient)
            }

            // Whether the timer should be running depends on whether we"re visible (as well as
            // whether we"re in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        /*override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                    // The user has started touching the screen.
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                    // The user has started a different gesture or otherwise cancelled the tap.
                }
                WatchFaceService.TAP_TYPE_TAP ->
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(applicationContext, R.string.message, Toast.LENGTH_SHORT)
                        .show()
            }
            invalidate()
        }*/

        @RequiresApi(Build.VERSION_CODES.S)
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            // TODO: Step 5, OnTapCommand()
            Log.d("Complications!:", "OnTapCommand()")
            when (tapType) {
                TAP_TYPE_TAP -> {
                    val tappedComplicationId: Int = getTappedComplicationId(x, y)
                    if (tappedComplicationId != -1) {
                        onComplicationTap(tappedComplicationId)
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        private fun getTappedComplicationId(x: Int, y: Int): Int {
            var complicationId: Int
            var complicationData: ComplicationData?
            var complicationDrawable: ComplicationDrawable
            val currentTimeMillis = System.currentTimeMillis()
            for (i in COMPLICATION_IDS.indices) {
                complicationId = COMPLICATION_IDS[i]
                complicationData = mActiveComplicationDataSparseArray!![complicationId]
                if (complicationData != null
                    && complicationData.isActive(currentTimeMillis)
                    && complicationData.type != ComplicationData.TYPE_NOT_CONFIGURED
                    && complicationData.type != ComplicationData.TYPE_EMPTY
                ) {
                    complicationDrawable = mComplicationDrawableSparseArray!![complicationId]
                    val complicationBoundingRect = complicationDrawable.bounds
                    if (complicationBoundingRect.width() > 0) {
                        if (complicationBoundingRect.contains(x, y)) {
                            return complicationId
                        }
                    } else {
                        Log.e("Complications!:","Not a recognized complication id.")
                    }
                }
            }
            return -1
        }

        private fun onComplicationTap(complicationId: Int) {
            // TODO: Step 5, onComplicationTap()
            Log.d("Complications!:", "onComplicationTap()")
            val complicationData = mActiveComplicationDataSparseArray!![complicationId]
            if (complicationData != null) {
                if (complicationData.tapAction != null) {
                    try {
                        complicationData.tapAction.send()
                    } catch (e: CanceledException) {
                        Log.e("Complications!:", "onComplicationTap() tap action error: $e")
                    }
                } else if (complicationData.type == ComplicationData.TYPE_NO_PERMISSION) {

                    // Watch face does not have permission to receive complication data, so launch
                    // permission request.
                    val componentName = ComponentName(
                        applicationContext, MyWatchFace2::class.java
                    )
                    val permissionRequestIntent =
                        ComplicationHelperActivity.createPermissionRequestHelperIntent(
                            applicationContext, componentName
                        )
                    startActivity(permissionRequestIntent)
                }
            } else {
                Log.d("Complications!:", "No PendingIntent for complication $complicationId.")
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            /* Scale loaded background image (more efficient) if surface dimensions change. */
            val scale = width.toFloat() / mBackgroundBitmap.width.toFloat()
            val scaleH = height.toFloat() / mBackgroundBitmap.height.toFloat()

            mBackgroundBitmap = Bitmap.createScaledBitmap(
                mBackgroundBitmap,
                (mBackgroundBitmap.width * scale).toInt(),
                (mBackgroundBitmap.height * scaleH).toInt(), true
            )
            mYOffset = mBackgroundBitmap.height / 2f

            //4
            val sizeOfComplication = width / 4
            val midpointOfScreen = width / 2

            val horizontalOffset = (midpointOfScreen - sizeOfComplication) / 2
            val verticalOffset = midpointOfScreen - sizeOfComplication / 2

            val leftBounds =  // Left, Top, Right, Bottom
                Rect(
                    horizontalOffset,
                    verticalOffset,
                    horizontalOffset + sizeOfComplication,
                    verticalOffset + sizeOfComplication
                )

            val leftComplicationDrawable = mComplicationDrawableSparseArray!![LEFT_COMPLICATION_ID]
            leftComplicationDrawable.bounds = leftBounds

            val rightBounds =  // Left, Top, Right, Bottom
                Rect(
                    midpointOfScreen + horizontalOffset,
                    verticalOffset,
                    midpointOfScreen + horizontalOffset + sizeOfComplication,
                    verticalOffset + sizeOfComplication
                )

            val rightComplicationDrawable =
                mComplicationDrawableSparseArray!![RIGHT_COMPLICATION_ID]
            rightComplicationDrawable.bounds = rightBounds
            //4
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun onDraw(canvas: Canvas, bounds: Rect) {
            // Draw the background.
            if (mAmbient) {
                canvas.drawColor(Color.BLACK)
            } else {
                canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, mBackgroundPaint)
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            val now = System.currentTimeMillis()
            mCalendar.timeInMillis = now

            val text = if (mAmbient)
                String.format(
                    "%d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE)
                )
            else
                String.format(
                    "%d:%02d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE), mCalendar.get(Calendar.SECOND)
                )
            canvas.drawText(text, mYOffset, mYOffset, mTextPaint)

            drawComplications(canvas, now)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        private fun drawComplications(canvas: Canvas, currentTimeMillis: Long) {
            // TODO: Step 4, drawComplications()
            var complicationId: Int
            var complicationDrawable: ComplicationDrawable
            for (i in 0 until COMPLICATION_IDS.size) {
                complicationId = COMPLICATION_IDS[i]
                complicationDrawable = mComplicationDrawableSparseArray!![complicationId]
                complicationDrawable.draw(canvas, currentTimeMillis)
            }
        }


        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()

                // Update time zone in case it changed while we weren"t visible.
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }

            // Whether the timer should be running depends on whether we"re visible (as well as
            // whether we"re in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        private fun registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@MyWatchFace2.registerReceiver(mTimeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@MyWatchFace2.unregisterReceiver(mTimeZoneReceiver)
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)

            // Load resources that have alternate values for round watches.
            val resources = this@MyWatchFace2.resources
            val isRound = insets.isRound
            mXOffset = resources.getDimension(
                if (isRound)
                    R.dimen.digital_x_offset_round
                else
                    R.dimen.digital_x_offset
            )

            val textSize = resources.getDimension(
                if (isRound)
                    R.dimen.digital_text_size_round
                else
                    R.dimen.digital_text_size
            )

            mTextPaint.textSize = textSize
        }

        /**
         * Starts the [.mUpdateTimeHandler] timer if it should be running and isn"t currently
         * or stops it if it shouldn"t be running but currently is.
         */
        private fun updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer should
         * only run when we"re visible and in interactive mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !isInAmbientMode
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }
}