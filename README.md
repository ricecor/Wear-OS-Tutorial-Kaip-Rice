# Wear-OS-Tutorial-Kaip-Rice

This page is a tutorial on how to get started with Wear OS and to get you on the path of starting you first Wear OS watch face.

# Getting Started
To start, we will be working with Kotlin through the Intelij IDE with Android Studio. First, you will have to download Android Studio (Link: https://developer.android.com/studio) This will be the tool we use to creat the watch face.

# First Time Setup
When you first launch the app you will click New Project at the top right of the first window. You will then be presented with a couple of different types of templates. Click on Wear OS shown here:
<img width="899" alt="Screen Shot 2021-11-21 at 2 41 52 PM" src="https://user-images.githubusercontent.com/59663943/144114374-6d616114-6a07-4aff-b304-7e8eb258b90c.png">

For this tutorial we are going to start with the watch face template. After selecting that click Next. On this window you can name your project and where to save it. Be sure to have the languge selection be Kotlin as shown below:
<img width="901" alt="Screen Shot 2021-11-21 at 2 51 28 PM" src="https://user-images.githubusercontent.com/59663943/144114529-71c36a82-9046-4d23-9379-5a1a292c3cce.png">

After the project loads you will have to select your simulator. This is going to be the watch face that you will run and test you face on. To do this, you must go to the tools drop down and select AVD Manager. After selecting that in the pop up select create Virtual device in the bottom left of the pop up. Then select Wear OS and for this demo we will be using the Wear OS Round as our simulator:
<img width="1002" alt="Screen Shot 2021-11-21 at 2 59 30 PM" src="https://user-images.githubusercontent.com/59663943/144115163-7fc5dac9-d3d0-42f0-bf5e-791c59b7c335.png">

After just select the most resent download and accept the license.

Then the installation will start. After that is done run your simulator with the green arrow at the top of the page.
NOTE: There may be an error in your Manifest.xml file when running the simulator. To fix this under the service heading put: 
```
android:exported="true">
```
With the greater than sign closing the service block.

One more note to get started. To launch the project make sure you edit your configuration and make launch option Nothing since there is no activity in a watch face to launch. See image below:
<img width="1075" alt="Screen Shot 2021-11-30 at 2 35 04 PM" src="https://user-images.githubusercontent.com/59663943/144116964-916babc9-ef32-47f0-84b2-0894687f266b.png">


# Analog Watch Face Tutorial

Now to actually make your own personal analog watch face. Watch this short tutorial for a quick rundown on how to change these componantes on your own watch face.

[Tutorial Video](https://youtu.be/oC6_8MoG5pc)

## Step-by-step
#### Background 
To start lets change the background image. This is pretty simple. First find the drawable folder in your directory and put the image you want in there. The file can be a .png or .jpeg. Make sure you know the name of the file. 
Next in 
```
private fun initializeBackground()
```
Find mBackgroundBitmap and at the end of that line you will see the defult dog picture. Put the name of your picture in place of that. Here is the line from the video:
```
mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background_image)
```

### Styling
For styling you are going to be working under the same function as before. The lines you will be looking for are:
```
mWatchHandHighlightColor = it.getVibrantColor(Color.GREEN)
mWatchHandColor = it.getLightVibrantColor(Color.RED)
mWatchHandShadowColor = it.getDarkMutedColor(Color.GREEN)
```
Change the colors at the end of the line to whatever you feel like that goes best with your theme. Be sure to also change the lines under the initializeWatchFace() function to the same colors. This function is right under the initializeBackground() function and the lines you want to change are:
```
mWatchHandColor = Color.RED
mWatchHandHighlightColor = Color.GREEN
mWatchHandShadowColor = Color.GREEN
```

### Hour Numbers

Now for the hour indicators it will be two lines of code that you have to write under the drawWatchFace(canvas: Canvas) function. In the for loop labeled 
```
for (tickIndex in 0..11)
```
at the top of the file you must go to the bottom and write:
```
val hour = (tickIndex).toString()
canvas.drawText(hour, (mCenterX + innerX), (mCenterY + innerY), mTickAndCirclePaint)
```
This uses the tickIndex to put the strings on screen with the canvas actually draw the text onto the watch face.


# Digital Watch Face Tutorial

[Youtube Tutorial for Digital Clock](https://www.youtube.com/watch?v=5iJZ_JTUXZc)

## Step-by-step
### Creating a new face
Create a new watchface by navigating to File>New>Wear>Watch Face, change the style to "Digital" and click "Finish"


### Update the manifest
Open up the manifest file and locate the watchface you just created. Then insert
```
android:exported="true"
```
under android:label. Go ahead and change the label to refelct what you want your new watch face to be called.


### Add your own font
Create a new assets folder in your project directory and add a new directory to the folder called "font". You can now add your .ttf file of choice into this folder. Locate 
```
val NORMAL_TYPEFACE = Typeface.create(SANS_SERIF, Typeface.NORMAL)
```
at the top of your main file and remove that line. Scroll down to your onCreate() method and add this line:
```
val NORMAL_TYPEFACE = Typeface.createFromAsset(getBaseContext().getAssets(), "font/blockgame.ttf")
```
replace "blockgame.ttf" with the name of your font file


### Changing the background
Drag an image file into the "Drawable" folder. Then add this variable to the top of your inner class:
```
private lateinit var mBackgroundBitmap: Bitmap
```
and paste this code into your onCreate() method:
```
mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.windows)
```
replacing "windows" with the name of your picture file. Navigate to the onDraw() method and replace the "canvas.drawRect(...)" statement with
```
canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, mBackgroundPaint)
```
If your background doesn't fill the screen completely, you'll need to add an onSurfaceChanged() method to adjust the image to the screen of your device:
```
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
        }
```
This will dynamically adjust your background image.


### Adjusting positioning
For this tutorial, we are going to center our text on the screen, add this to your "mTextPaint" object:
```
textAlign = Paint.Align.CENTER
```
Now that we changed the alignment, we need to adjust the positioning of the text. Go to your onSurfaceChanged() method and add
```
mYOffset = mBackgroundBitmap.height / 2f
```
to the bottom. Then go back to onDraw() and change the X coordinate of your text to also use "mYOffset"
```
canvas.drawText(text, mYOffset, mYOffset, mTextPaint)
```

You should now have a customized digital watchface!


# Complications Feature
Complications is a feature in WearOS that allows users to to add quick access and information to be displayed on the watch face from different apps. For example, you can display the battery life of the watch, calendar events, and reminders all on your watchface.

Our tutorial is based on an existing tutorial from Android https://developer.android.com/codelabs/complications#-1. This tutorial is written in Java and requires you to download their project file to follow along. We have converted the code to Kotlin and designed our tutorial to work with **your** project.

[Tutorial video](https://youtu.be/SRBprS7IMqc)

## Adding required resources
Adding complications to your watchface requires extra files to display correctly. Go ahead and download these files and add them to the "drawable" directory in your project: <br />
[add_complication.png](WatchFaceTutorial/app/src/main/res/drawable-nodpi/add_complication.png) <br />
[added_complication.png](WatchFaceTutorial/app/src/main/res/drawable-nodpi/added_complication.png) <br />
[custom_complication_styles.xml](WatchFaceTutorial/app/src/main/res/drawable-nodpi/custom_complication_styles.xml) <br />
[settings_watch_face_preview_arms_and_ticks.xml](WatchFaceTutorial/app/src/main/res/drawable-nodpi/settings_watch_face_preview_arms_and_ticks.xml) <br />
[settings_watch_face_preview_background.xml](WatchFaceTutorial/app/src/main/res/drawable-nodpi/settings_watch_face_preview_background.xml) <br />
[settings_watch_face_preview_highlight.xml](WatchFaceTutorial/app/src/main/res/drawable-nodpi/settings_watch_face_preview_highlight.xml) <br />

Next, if you don't already have one, you'll want to create a layout directory by right clicking on your "res" directory and navigating to New>Android Resource Directory and changing the name to "layout" and selecting "layout" from the first dropdown. Once you have created your new "layout" directory, add this file to it: <br />
[activity_config.xml](WatchFaceTutorial/app/src/main/res/layout/activity_config.xml) <br />

The last file you will need to download is the ComplicationConfigActivity.kt file and add this to the directory with your main class, confirming it is imported as a Kotlin Class: <br />
[ComplicationConfigActivity.kt](WatchFaceTutorial/app/src/main/java/com/example/myfirstwatchface/ComplicationConfigActivity.kt) <br />
Make sure to adjust the package to reflect your own. You will also need to replace all instances of "MyWatchFace" with the name of your main kotlin file.


## Modifying the main kotlin class
Begin by adding the following import:
```
import com.example.myfirstwatchface.ComplicationConfigActivity.ComplicationLocation
```
replacing "myfirstwatchface" with what is included in your package name. 


### Step 1
Then add the following code to the top of your class:
```
    private val LEFT_COMPLICATION_ID = 0
    private val RIGHT_COMPLICATION_ID = 1

    private val COMPLICATION_IDS = kotlin.intArrayOf(LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID)

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
```

### Step 2
At the top of your inner class, add this snippet to create two sparse arrays:
```
        private var mActiveComplicationDataSparseArray: SparseArray<ComplicationData>? = null
        private var mComplicationDrawableSparseArray: SparseArray<ComplicationDrawable>? = null
```


### Step 3
In your onCreate() method, add this statement to initialize your complications:
```
initializeComplications()
```


### Step 4
Add this code to the onPropertiesChanged() method. This should cause an error that can be quickly resolved by adding "@RequiresApi(Build.VERSION_CODES.S)" above the function declaration. This should be repeated for all errors of the like.
```
            var complicationDrawable: ComplicationDrawable?

            for (i in COMPLICATION_IDS.indices) {
                complicationDrawable =
                    mComplicationDrawableSparseArray!![COMPLICATION_IDS[i]]
                if (complicationDrawable != null) {
                    complicationDrawable.setLowBitAmbient(mLowBitAmbient)
                    complicationDrawable.setBurnInProtection(mBurnInProtection)
                }
            }
```


### Step 5
In onAmbientModeChanged(), add this code. This will make your complications react appropriately when your watch enters ambient mode.
```
            var complicationDrawable: ComplicationDrawable

            for (i in COMPLICATION_IDS.indices) {
                complicationDrawable =
                    mComplicationDrawableSparseArray!![COMPLICATION_IDS[i]]
                complicationDrawable.setInAmbientMode(mAmbient)
            }
```


### Step 6
To handle where your complications are positioned on the screen, add this large chunk of code to the onSurfaceChanged() function:
```
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
```


### Step 7
If your watchface doesn't currently perform any actions when you tap the screen, replace the inside of onTapCommand() with
```
            Log.d("Complications!:", "OnTapCommand()")
            when (tapType) {
                TAP_TYPE_TAP -> {
                    val tappedComplicationId: Int = getTappedComplicationId(x, y)
                    if (tappedComplicationId != -1) {
                        onComplicationTap(tappedComplicationId)
                    }
                }
            }
```
Otherwise you can only add the code that follows "TAP_TYPE_TAP".


### Step 8
In onDraw(), insert this code in-between drawBackground(canvas) and drawWatchFace(canvas):
```
            drawComplications(canvas, now)
```


### Step 9
You will now need to add multiple functions to the bottom of the inner class to support the creation and function of your complications:
```
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
            setActiveComplications(LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID)
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
            Log.d("Complications!:", "onComplicationTap()")
            val complicationData = mActiveComplicationDataSparseArray!![complicationId]
            if (complicationData != null) {
                if (complicationData.tapAction != null) {
                    try {
                        complicationData.tapAction.send()
                    } catch (e: PendingIntent.CanceledException) {
                        Log.e("Complications!:", "onComplicationTap() tap action error: $e")
                    }
                } else if (complicationData.type == ComplicationData.TYPE_NO_PERMISSION) {

                    // Watch face does not have permission to receive complication data, so launch
                    // permission request.
                    val componentName = ComponentName(
                        applicationContext, MyWatchFace::class.java
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

        @RequiresApi(Build.VERSION_CODES.S)
        private fun drawComplications(canvas: Canvas, currentTimeMillis: Long) {
            var complicationId: Int
            var complicationDrawable: ComplicationDrawable
            for (i in 0 until COMPLICATION_IDS.size) {
                complicationId = COMPLICATION_IDS[i]
                complicationDrawable = mComplicationDrawableSparseArray!![complicationId]
                complicationDrawable.draw(canvas, currentTimeMillis)
            }
        }
```


## Updating the manifest
You should now be able to run your watchface and see that two complications appear. You won't have the ability to modify or configure them yet because we need to first add to the manifest file. In the AndroidManifest.xml, insert two new metadata tags to your watchface service:
```
<meta-data
                android:name="com.google.android.wearable.watchface.wearableConfigurationAction"
                android:value="androidx.wear.watchface.editor.action.WATCH_FACE_EDITOR" />
            <meta-data
                android:name="com.google.android.wearable.watchface.companionBuiltinConfigurationEnabled"
                android:value="true" />
```

At the bottom of the application tag, add two new activity tags:
```
<activity android:name="android.support.wearable.complications.ComplicationHelperActivity"/>

        <activity
            android:name=".ComplicationConfigActivity"
            android:label="My Analog"
            android:exported="true">
            <intent-filter>
                <action android:name="androidx.wear.watchface.editor.action.WATCH_FACE_EDITOR" />

                <category android:name="com.google.android.wearable.watchface.category.WEARABLE_CONFIGURATION" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
```

Run your program one more time and you should see that your watchface now has a gear icon underneath it when you enter watchface selection. If you click on that gear you should be able to select which data you want in each complication.

That is it! You should now have a watchface capable of displaying complications.
