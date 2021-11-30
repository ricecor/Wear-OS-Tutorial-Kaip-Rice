# Wear-OS-Tutorial-Kaip-Rice

This page is a tutorial on how to get started with Wear OS and to get you on the path of starting you first Wear OS watch face.

# Getting Started
To start, we will be working with Kotlin through the Intelij IDE with Android Studio. First, you will have to download Android Studio (Link: https://developer.android.com/studio) This will be the tool we use to creat the watch face.

# First Time Setup
When you first launch the app you will click New Project at the top right of the first window. You will then be presented with a couple of different types of templates. Click on Wear OS shown here:
<img width="899" alt="Screen Shot 2021-11-21 at 2 41 52 PM" src="https://user-images.githubusercontent.com/59663943/144114374-6d616114-6a07-4aff-b304-7e8eb258b90c.png">

For this tutorial we are going to start with the watch face template. After selecting that click Next. On this window you can name your project and where to save it. Be sure to have the languge selection be Kotlin as shown below:
<img width="901" alt="Screen Shot 2021-11-21 at 2 51 28 PM" src="https://user-images.githubusercontent.com/59663943/144114529-71c36a82-9046-4d23-9379-5a1a292c3cce.png">

After the project loads you will have to select your simulator. This is going to be the watch face that you will run and test you face on. To do this, you must go to the tools drop down and select AVD Manager:

After selecting that in the pop up select create Virtual device in the bottom left of the pop up. Then select Wear OS and for this demo we will be using the Wear OS Round as our simulator:

After just select the most resent download and accept the license:

Then the installation will start. After that is done run your simulator with the green arrow at the top of the page.
NOTE: There may be an error in your Manifest.xml file when running the simulator. To fix this under the service heading put: 
```
android:exported="true">
```
With the greater than sign closing the service block.
