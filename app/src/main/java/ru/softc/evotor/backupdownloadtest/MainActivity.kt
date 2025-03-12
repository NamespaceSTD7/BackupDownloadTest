package ru.softc.evotor.backupdownloadtest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import ru.softc.evotor.backupdownloadtest.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editText.setText("http://debt.evotor.tech/backup/2f5bef96-31ba-4098-8c70-97a4c989e985.bin.gz")

        binding.buttonDownload.setOnClickListener {
            binding.progressBar.isVisible = true
            thread {
                try {
                    downloadFile(binding.editText.text.toString())
                } catch (e: Exception) {
                    runOnUiThread {
                        //Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                        AlertDialog.Builder(this).setMessage(e.message).show()
                    }
                }
                runOnUiThread {
                    binding.progressBar.isVisible = false
                }
            }
        }
    }

    private fun downloadFile(url: String) {
        val response = ServerAPI.API.downloadFile(url).execute()

        if (!response.isSuccessful) {
            throw RuntimeException("${response.code() }: ${response.message()}")
        } else {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Код ответа: ${response.code()}\nЗаголовки: ${response.headers()}",
                    Toast.LENGTH_LONG
                ).show()
//                AlertDialog.Builder(this).setMessage("Код ответа: ${response.code()}\n" +
//                        "Заголовки:\n${response.headers()}").show()
            }
        }

        val body = requireNotNull(response.body()) { "Пустое тело ответа" }

        val tempFile = File(getExternalFilesDir(null), "download.bin")

        body.byteStream().use { fis ->
            FileOutputStream(tempFile).use { fos ->
                fis.copyTo(fos)
            }
        }

        runOnUiThread {
            AlertDialog.Builder(this).setMessage(
                "Файл сохранен в ${tempFile.absolutePath}\n" +
                        "Content-Length: ${response.headers().get("Content-Length")} \n" +
                        "Размер скачанного файла: ${tempFile.length()}"
            ).show()
//            Toast.makeText(this, "Файл сохранен в ${tempFile.absolutePath}\n" +
//                    "Content-Length: ${response.headers().get("Content-Length")} \n" +
//                    "Размер скачанного файла: ${tempFile.length()}", Toast.LENGTH_LONG).show()
        }

    }
}