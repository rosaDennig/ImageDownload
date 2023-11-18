package at.interactivecuriosity.imagedownload
import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownLoadImageService : IntentService("DownloadImageService") {

    override fun onHandleIntent(intent: Intent?) {
        val urlString = intent?.getStringExtra("urlString")
        val fileName = intent?.getStringExtra("fileName")
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            val file = File(getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }


            // Erstellen des Intents für den Broadcast
            val broadcastIntent = Intent("at.interactivecuriosity.imagedownload.DOWNLOAD_AKTION")
            broadcastIntent.putExtra("filePath", file.absolutePath)

            // Senden des Broadcasts
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

        } catch (e: Exception) {
            e.printStackTrace()

            val broadcastIntent = Intent("at.interactivecuriosity.imagedownload.DOWNLOAD_AKTION")
            broadcastIntent.putExtra("filePath", "fail")
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

        }

    }

}