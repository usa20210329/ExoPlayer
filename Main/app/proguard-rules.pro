# okio
-dontwarn okio.**

# apache
-dontwarn org.apache.**

# Add this global rule
-keepattributes Signature

# Firebase
-keepclassmembers class com.fongmi.android.ltv.bean.Bean { *; }
-keepclassmembers class com.fongmi.android.ltv.bean.Channel { *; }
-keepclassmembers class com.fongmi.android.ltv.bean.Config { *; }
-keepclassmembers class com.fongmi.android.ltv.bean.Config$Core { *; }
-keepclassmembers class com.fongmi.android.ltv.bean.Type { *; }

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

#Gson
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}