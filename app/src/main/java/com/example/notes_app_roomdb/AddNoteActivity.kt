package com.example.notes_app_roomdb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.Date

class AddNoteActivity : AppCompatActivity() {
     private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note: Note
    private lateinit var oldNote: Note
    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor(R.color.nav_color)

        try{
            oldNote = intent.getSerializableExtra("current_note") as Note
            binding.titleET.setText(oldNote.title)
            binding.noteET.setText(oldNote.content)
            isUpdate = true
        }catch (e: Exception){
            e.printStackTrace()
        }

        if(isUpdate){
            binding.deleteBTN.visibility = View.VISIBLE
        }else{
            binding.deleteBTN.visibility = View.INVISIBLE
        }

        binding.saveBTN.setOnClickListener{
            val title = binding.titleET.text.toString()
            val noteContent = binding.noteET.text.toString()

            if(title.isNotEmpty() && noteContent.isNotEmpty()){
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
                if(isUpdate){
                    note = Note(oldNote.id, title, noteContent, formatter.format(Date()))
                }else{
                    note = Note(null, title, noteContent, formatter.format(Date()))
                }
                var intent = Intent()
                intent.putExtra("note", note)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@AddNoteActivity, "please enter some data", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
        binding.deleteBTN.setOnClickListener {
            var intent = Intent()
            intent.putExtra("note", oldNote)
            intent.putExtra("delete_note", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.backBTN.setOnClickListener {
            onBackPressed()
        }


    }

    private fun setNavigationBarColor(colorResource: Int) {
        window.navigationBarColor = resources.getColor(colorResource, theme)
    }
}