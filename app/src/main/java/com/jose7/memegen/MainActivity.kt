package com.jose7.memegen

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val pickImage = 100
    private lateinit var imageView: ImageView
    private lateinit var saveBtn: Button
    private lateinit var shareBtn: Button
    private var currentImage = ""

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val selectedImage = it.data?.data
            imageView.setImageURI(selectedImage)
            saveBtn.isEnabled = true
            shareBtn.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        } else {
            Toast.makeText(this, "Storage Permission not granted", Toast.LENGTH_LONG).show()
        }

        imageView = findViewById(R.id.ivMeme)
        val textView1 = findViewById<TextView>(R.id.tvTextView1)
        val textView2 = findViewById<TextView>(R.id.tvTextView2)

        val editText1 = findViewById<EditText>(R.id.etTopText)
        val editText2 = findViewById<EditText>(R.id.etBottomText)

        val tryBtn = findViewById<Button>(R.id.btnTry)

        val loadBtn = findViewById<Button>(R.id.btnLoad)
        saveBtn = findViewById(R.id.btnSave)
        shareBtn = findViewById(R.id.btnShare)

        saveBtn.isEnabled = false
        shareBtn.isEnabled = false

        loadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getResult.launch(intent)
        }

        saveBtn.setOnClickListener {
            val content: View = findViewById(R.id.lay)
            val bitmap = getScreenShot(content)
            currentImage = "meme" + System.currentTimeMillis() + ".png"
            store(bitmap, currentImage)
            shareBtn.isEnabled = true
        }

        shareBtn.setOnClickListener { shareImage(currentImage) }

        tryBtn.setOnClickListener {
            textView1.text = editText1.text
            textView2.text = editText2.text

            editText1.setText("")
            editText2.setText("")
        }
    }

    private fun getScreenShot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.getDrawingCache())
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun store(bm: Bitmap, fileName: String) {
        val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(dirPath, fileName)

        try {
            dirPath.mkdirs()
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
        }
        catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error Saving!", Toast.LENGTH_LONG).show()
        }
    }

    private fun shareImage(fileName: String) {
        val dirPath = Environment.getExternalStorageState() + "/MEME"
        val uri = Uri.fromFile(File(dirPath, fileName))

        val intent = Intent(Intent.ACTION_SEND, )
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"

        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        try {
            startActivity(Intent.createChooser(intent, "Share via"))
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No Sharing App found!", Toast.LENGTH_LONG).show()
        }
    }
}