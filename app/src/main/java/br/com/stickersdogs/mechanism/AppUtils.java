package br.com.stickersdogs.mechanism;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitary class for generally application used methods.
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    /**
     * Gets the application version.
     * @param context Application context.
     * @return Application version.
     */
    public static String version(@NonNull final Context context) {
        String result = "Unknown";

        try {
            // Get app version
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            result = packageInfo.versionName;
        } catch (Exception e) {
            Log.e(TAG, "Error getting app version", e);
        }
        return result;
    }

    /**
     * Gets the device's ID.
     * @param context Application context
     * @return Android ID
     */
    public static String deviceId(@NonNull final Context context) {
        String androidID = "";

        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            Log.e(TAG, "Error retrieving android ID", ex);
        }

        return androidID;
    }

    public static String formatCurrency(Double val) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return numberFormat.format(val);
    }
}
