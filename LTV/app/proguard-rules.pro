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

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}