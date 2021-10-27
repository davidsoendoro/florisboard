package com.kokatto.kobold.utility

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.Telephony

class ThirdAppUtility {
    companion object {
        fun checkAppInstalledOrNot(context: Context, packageName: String): Boolean {
            var packageManager: PackageManager = context.packageManager
            try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }
        }

        fun openApplicationActivity(context: Context, packageName: String) {
            if (checkAppInstalledOrNot(context, packageName)) {
                var packMan: PackageManager = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.setPackage(packageName)
                val launchables: List<ResolveInfo> = packMan.queryIntentActivities(intent, 0)

                if (launchables.isNotEmpty()) {
                    val activity: ActivityInfo = launchables.get(0).activityInfo
                    val name = ComponentName(
                        activity.applicationInfo.packageName,
                        activity.name
                    )
                    val intentToLauch = Intent(Intent.ACTION_MAIN)
                    intentToLauch.addCategory(Intent.CATEGORY_LAUNCHER)
                    intentToLauch.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    intentToLauch.component = name
                    context.startActivity(intentToLauch)
                }
            } else {
                // Bring user to the market or let them choose an app?
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("market://details?id=$packageName")
                context.startActivity(intent)
            }
        }

        fun openWhatsappAndDirectToNumber(phoneNo: String, message: String, context: Context, packageName: String) {

            if (checkAppInstalledOrNot(context, packageName)) {
                val url = Uri.parse("https://api.whatsapp.com/send?phone=${phoneNo}&text=${message}")
                val intent = Intent(Intent.ACTION_VIEW, url)
                intent.setPackage(packageName)
                context.startActivity(intent)
            } else {
                // Bring user to the market or let them choose an app?
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("market://details?id=$packageName")
                context.startActivity(intent)
            }
        }


        fun openSendSMS(context: Context, phoneNo: String, message: String = "") {
            val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context)
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.putExtra("address", parsePhoneWithCountryCode(phoneNo, "+", "62"))
            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName)
            }
            context.startActivity(sendIntent)
        }

        fun openPhoneDial(context: Context, phoneNo: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${parsePhoneWithCountryCode(phoneNo, "+", "62")}")
            context.startActivity(intent)
        }

        fun openGmail(context: Context, emailAddress: String) {
            val packageName = "com.google.android.gm"

            if (checkAppInstalledOrNot(context, packageName)) {
                val sendIntent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf(emailAddress) //Add multiple recipients here
                sendIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
                sendIntent.type = "text/html"
                sendIntent.setPackage(packageName)
                context.startActivity(sendIntent)
            } else {
                // Bring user to the market or let them choose an app?
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("market://details?id=$packageName")
                context.startActivity(intent)
            }
        }

        fun parsePhoneWithCountryCode(phoneNo: String, prefix: String = "+", countryCode: String = "62"): String {
            return if (phoneNo.isNullOrBlank()) {
                ""
            } else if (phoneNo.substring(0, 1).equals("0", true)) {
                String.format("%s%s%s", prefix, countryCode, phoneNo.substring(1, phoneNo.length))
            } else if (phoneNo.substring(0, 2).equals(countryCode, true)) {
                String.format("%s%s", prefix, phoneNo)
            } else {
                String.format("%s%s", prefix, phoneNo)
            }
        }

    }
}
