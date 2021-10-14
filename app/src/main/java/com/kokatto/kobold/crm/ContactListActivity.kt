package com.kokatto.kobold.crm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityContactBinding
import com.kokatto.kobold.extension.showSnackBar

class ContactListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    private var contactEmpty: Boolean = true

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showContactImport()
            } else {
                showSnackBar("Import dari phonebook tidak bisa digunakan", R.color.snackbar_error)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.koboldContactContent.apply {

            buttonAddContact.setOnClickListener {
                val dialogContactMenu = DialogContactMenu().newInstance()
                dialogContactMenu.show(supportFragmentManager, dialogContactMenu.TAG)

                dialogContactMenu.onImportClick = {
                    dialogContactMenu.dismiss()
                    checkContactImportView()
                }

                dialogContactMenu.onManualClick = {
                    dialogContactMenu.dismiss()
                    showContactManualview()
                }

            }
        }
    }

    private fun showContactManualview() {
        showSnackBar("Not yet implemented", R.color.snackbar_error)
    }

    private fun checkContactImportView() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showContactImport()
        } else requestReadContactPermission()
    }

    private fun requestReadContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun showContactImport() {
        startActivity(Intent(this, ContactImportActivity::class.java))
        finish()
    }


}
