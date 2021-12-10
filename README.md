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

https://youtu.be/oC6_8MoG5pc

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

Youtube Tutorial for Digital Clock:
https://www.youtube.com/watch?v=5iJZ_JTUXZc

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
Compliications is a feature in WearOS that allows users to to add quick access and information to be displayed on the watch face from different apps. For example as you can see in the screenshot you can display the battery life of the watch and reminders.

These are just two examples of many different combinations that the user can use. It is complex to add these to your watch in android studio. That being said if you clone our repo you can check out the complication code and familiarize yourself with what needs to be done on your watch. This code is located in the analog watch folder. There is changes to the mywatchface.kt and the new complications.kt file. This a a very strong feature in WearOS so be sure to check out those files.
