package com.cms.checkprint.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.ArrayList

class MyPermissionUtil {
    fun isHavePermission(
        activity: Activity, requestCode: Int, vararg permissions: String
    ): Boolean {
        var granted = true
        val permissionsNeeded = ArrayList<String>()

        for (s in permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(activity, s)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted and hasPermission
            if (!hasPermission) {
                permissionsNeeded.add(s)
            }
        }

        return if (granted) {
            true
        } else {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toTypedArray(), requestCode)
            false
        }
    }

    fun isHavePermission(
        fragment: Fragment, requestCode: Int, vararg permissions: String
    ): Boolean {
        var granted = true
        val permissionsNeeded = ArrayList<String>()

        for (s in permissions) {
            val permissionCheck = ActivityCompat.checkSelfPermission(fragment.requireContext(), s)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted and hasPermission
            if (!hasPermission) {
                permissionsNeeded.add(s)
            }
        }

        return if (granted) {
            true
        } else {
            fragment.requestPermissions(permissionsNeeded.toTypedArray(), requestCode)
            false
        }
    }


    fun isPermissionGranted(
        requestCode: Int, permissionCode: Int, grantResults: IntArray
    ): Boolean {
        return if (requestCode == permissionCode) {
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else false
    }

}