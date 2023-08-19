package com.example.mediaplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val pickDirectoryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val uri = data?.data
            uri?.let { processSelectedDirectory(uri) }
        }
    }

    private lateinit var songList: ArrayList<String>
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var listView: ListView
    private lateinit var bpickd: Button
    private lateinit var buttonPlay: Button
    private  lateinit var song:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songList = ArrayList()
        listView = findViewById(R.id.listView)
        bpickd=findViewById(R.id.buttonPickDirectory)
        buttonPlay=findViewById(R.id.buttonPlay)
        bpickd.setOnClickListener {
            openDirectoryPicker()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            song = songList[position]

        }

        buttonPlay.setOnClickListener {
            playMusic(song)
            mediaPlayer?.start()
        }
    }

    private fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        pickDirectoryResultLauncher.launch(intent)
    }

    @SuppressLint("Range")
    private fun processSelectedDirectory(uri: android.net.Uri) {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
        )
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val path = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))
                songList.add(path)
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songList)
        listView.adapter = adapter
    }

    private fun playMusic(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(filePath)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
