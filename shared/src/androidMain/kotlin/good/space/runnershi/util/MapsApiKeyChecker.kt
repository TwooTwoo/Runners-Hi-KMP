package good.space.runnershi.util

import android.content.Context
import android.content.pm.PackageManager

object MapsApiKeyChecker {
    private const val META_DATA_KEY = "com.google.android.geo.API_KEY"
    private const val PLACEHOLDER_VALUE = "YOUR_API_KEY_HERE"

    /**
     * AndroidManifest.xml에서 Google Maps API 키를 읽어옵니다.
     * @return API 키가 설정되어 있고 유효하면 true, 그렇지 않으면 false
     */
    fun isApiKeySet(context: Context): Boolean {
        return runCatching {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val apiKey = appInfo.metaData?.getString(META_DATA_KEY)

            !apiKey.isNullOrBlank() && apiKey != PLACEHOLDER_VALUE
        }.getOrDefault(false)
    }
}
