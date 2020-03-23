# okio
-dontwarn okio.**

# apache
-keep class org.apache.** { *; }
-dontwarn org.apache.**

# Add this global rule
-keepattributes Signature

# Firebase
-keepclassmembers class com.fongmi.android.ltv.bean.Channel { *; }
-keepclassmembers class com.fongmi.android.ltv.bean.Config { *; }

# tvbus
-keep class com.tvbus.engine.** { *; }

# dueeeke
-keep class com.dueeeke.videoplayer.** { *; }
-dontwarn com.dueeeke.videoplayer.**

# ijk
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**
