package com.example.notes_app_roomdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes_app_roomdb.adaptors.NoteAdapter
import com.example.notes_app_roomdb.adaptors.RecBinAdapter
import com.example.notes_app_roomdb.databinding.ActivityRecycleBinBinding
import com.example.notes_app_roomdb.models.NoteViewModel

class RecycleBinActivity : AppCompatActivity(), NoteAdapter.DeleteIconChange {

    lateinit var binding: ActivityRecycleBinBinding
    private lateinit var adapter: RecBinAdapter
    private lateinit var viewModel: NoteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        viewModel.deletedNote.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }
        }

        binding.deletePButton.setOnClickListener {
            deletePermenent()
        }

        onLongPress(adapter.isLongClick)

    }


    private fun deletePermenent() {
        if(::adapter.isInitialized){
            val selectedNote = ArrayList(adapter.selectedNotes)
            viewModel.deleteSelectedNotes(selectedNote.map { it.id?:-1 })
            adapter.selectedNotes.clear()
            adapter.notifyDataSetChanged()
            adapter.isLongClick = false
            onLongPress(false)
        }
    }

    private fun initUI() {
        binding.recView.setHasFixedSize(true)
        binding.recView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = RecBinAdapter(this)
        binding.recView.adapter = adapter
    }

    override fun onLongPress(longPress: Boolean) {
        if(longPress){
            binding.deletePButton.visibility = View.VISIBLE
        }else{
            binding.deletePButton.visibility = View.INVISIBLE
        }
    }
}