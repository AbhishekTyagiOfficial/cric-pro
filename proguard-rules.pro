# ProGuard / R8 rules for CricPro App

-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt rules
-keep class * extends dagger.hilt.internal.UnsafeCasts

# Apache POI rules for Excel export
-dontwarn org.apache.poi.**
