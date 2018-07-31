package com.jchavez.telstockapp

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.jchavez.telstockapp.models.PhotoItem
import com.jchavez.telstockapp.retrofit.ApiService
import com.mvc.imagepicker.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photoitem_list.*
import kotlinx.android.synthetic.main.photoitem_list.*
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.ContextCompat
import java.io.File


class PhotoItemEditActivity : AppCompatActivity() {

    private lateinit var currentMode: String
    private var editId: Int = -1
    private lateinit var photoTitle: String
    private lateinit var uri: String
    private val apiService = ApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_item_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        1)
            } else {
                ImagePicker.pickImage(this)
            }
        }
        currentMode = intent.getStringExtra(ARG_MODE)

        val deleteButton = findViewById<FloatingActionButton>(R.id.deleteButton)

        if (currentMode == ARG_ADD_MODE) {
            title = resources.getString(R.string.add)
            deleteButton.visibility = View.GONE
        } else {
            title = resources.getString(R.string.edit)
            editId = intent.getIntExtra(ARG_ITEM_ID, -1)
            photoTitle = intent.getStringExtra(ARG_ITEM_TITLE)

            val titleEditText = findViewById<EditText>(R.id.titleEditText)
            titleEditText.setText(photoTitle)

            deleteButton.visibility = View.VISIBLE

            deleteButton.setOnClickListener { view ->
                showConfirmationMessage()
            }
        }

        setupAcceptButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data)
        val thumbnailImageView = findViewById<ImageView>(R.id.editImageView)
        val resultUri = ImagePicker.getImagePathFromResult(this, requestCode, resultCode, data)

        if (data != null || resultUri != null) {
            uri = "$resultUri"
            thumbnailImageView.setImageBitmap(bitmap)
        } else {
            thumbnailImageView.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_gallery))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun setupAcceptButton() {
        val acceptButton = findViewById<Button>(R.id.acceptButton)
        acceptButton.setOnClickListener {
            if (checkValidation()) {
                val titleEditText = findViewById<EditText>(R.id.titleEditText)
                if (currentMode == ARG_ADD_MODE) {
                    postPhoto(titleEditText.text.toString())
                } else {
                    patchPhoto(titleEditText.text.toString())
                }
            } else {
                showValidationError()
            }
        }
    }

    private fun showConfirmationMessage() {
        showAlertDialog {
            setTitle(resources.getString(R.string.alert))
            setMessage(resources.getString(R.string.deleteMessage))
            positiveButton {
                deletePhoto()
            }

            negativeButton {

            }
        }
    }

    private fun showAlertDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.dialogBuilder()
        val dialog = builder.create()
        dialog.show()
    }

    private fun AlertDialog.Builder.positiveButton(text: String = resources.getString(R.string.accept), handleClick: (which: Int) -> Unit = {}) {
        this.setPositiveButton(text, { dialogInterface, which-> handleClick(which) })
    }

    private fun AlertDialog.Builder.negativeButton(text: String = resources.getString(R.string.cancel), handleClick: (which: Int) -> Unit = {}) {
        this.setNegativeButton(text, { dialogInterface, which-> handleClick(which) })
    }

    private fun checkValidation() : Boolean {
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        return !titleEditText.text.isBlank()
    }

    private fun showValidationError() {
        Toast.makeText(this, resources.getString(R.string.validationFailedMessage), Toast.LENGTH_SHORT).show()
    }

    private fun postPhoto(title: String) {
        val photoItem = PhotoItem(
                editId,
                1,
                title,
                "http://placehold.it/600/92c952",
                "http://placehold.it/600/92c952")

        apiService.create(photoItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::successRequest, this::handleErrors)
    }

    private fun patchPhoto(title: String) {
        val photoItem = PhotoItem(
                editId,
                1,
                title,
                "http://placehold.it/600/92c952",
                "http://placehold.it/600/92c952")

        apiService.update(editId, photoItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::successRequest, this::handleErrors)
    }

    private fun deletePhoto() {
        apiService.destroy(editId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::successRequest, this::handleErrors)
    }

    private fun successRequest(result: PhotoItem) {
        Log.d(TAG_PHOTOS, "Got results!")
        Log.d(TAG_PHOTOS, result.toString())
        navigateUpTo(Intent(this, PhotoItemListActivity::class.java))
    }

    private fun handleErrors(error: Throwable?) {
        val code = error.toString()
        Log.e(TAG_ERROR, code)
        when (code) {
            "401" -> print(code)
            else -> print(code)
        }
    }

    companion object {
        const val ARG_ITEM_TITLE = "item_title"
        const val ARG_ADD_MODE = "add_mode"
        const val ARG_MODE = "mode"
        const val ARG_ITEM_ID = "item_id"
        const val TAG_PHOTOS = "PHOTOS"
        const val TAG_ERROR = "ERROR"
    }
}
