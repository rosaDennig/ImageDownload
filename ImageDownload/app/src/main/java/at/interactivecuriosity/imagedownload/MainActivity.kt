package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import at.interactivecuriosity.imagedownload.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg" // URL des herunterzuladenden Bildes
    private val fileName = "downloadedImage.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        downloadButton.setOnClickListener {
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("at.interactivecuriosity.imagedownload.DOWNLOAD_AKTION")
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter)
    }

    private val myReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getStringExtra("filePath")
            if (data == "fail"){
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Fehler beim Herunterladen", Toast.LENGTH_LONG).show()
                }
            }else{
                val bitmap = BitmapFactory.decodeFile(data)
                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                    Toast.makeText(this@MainActivity, "Bild heruntergeladen", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }

    private fun downloadImage(urlString: String, fileName: String) {
        val intent = Intent(this@MainActivity, DownLoadImageService::class.java)
        intent.putExtra("urlString",urlString)
        intent.putExtra("fileName",fileName)
        startService(intent)
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Download Start", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            runOnUiThread {
                imageView.setImageBitmap(null)
                Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
