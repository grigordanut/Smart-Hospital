package com.example.smarthospital;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

public class FingerPrintUtils {

    // Open the Security settings screen.

    public static void openSecuritySettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * Check if the device have supported the finger print scanner.
     *
     * @return true if device have the hardware.
     */
    public static boolean isSupportedHardware(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }
}
