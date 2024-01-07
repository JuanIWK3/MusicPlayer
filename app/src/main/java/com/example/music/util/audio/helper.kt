package com.example.music.util.audio

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun setupPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onPermissionsGranted: (() -> Unit)? = null
) {

    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        onPermissionsGranted?.invoke()
    } else {
        launcher.launch(permissions)
    }
}

fun showPermissionsRationalDialog(
    context: Context,
    dialogText: String,
    errorText: String,
    @StringRes okButtonTextResId: Int,
    @StringRes cancelButtonTextResId: Int,
    packageName: String
) {
    AlertDialog.Builder(context).setMessage(dialogText)
        .setPositiveButton(okButtonTextResId) { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                context.startActivity(intent)
            } catch (exp: Exception) {
                AlertDialog.Builder(context).setMessage(errorText)
            }
        }.setNegativeButton(cancelButtonTextResId) { dialog, _ ->
            dialog.dismiss()
        }.show()
}