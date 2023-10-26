package com.example.notes_app_roomdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
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

        setNavigationBarColor(R.color.nav_color)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]
        viewModel.deletedNote.observe(this) { list ->
            list?.let {
                adapter.updateList(list)
            }
        }
        viewModel.deletedNote.observe(this, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                binding.noNoteTV.visibility = View.VISIBLE
            } else {
                binding.noNoteTV.visibility = View.INVISIBLE
            }
        })


        binding.deletePButton.setOnClickListener {
            deletePermenent()
        }

       binding.restoreButton.setOnClickListener {
            restoreNote()
        }

        onLongPress(adapter.isLongClick)

        binding.backBTN.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun restoreNote() {
        if(::adapter.isInitialized){
            val selectedNote = ArrayList(adapter.selectedNotes)
            viewModel.restoreSelectedNotes(selectedNote.map { it.id?:-1 })
            adapter.selectedNotes.clear()
            adapter.notifyDataSetChanged()
            adapter.isLongClick = false
            onLongPress(false)
        }
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
            binding.restoreButton.visibility = View.VISIBLE
        }else{
            binding.deletePButton.visibility = View.INVISIBLE
            binding.restoreButton.visibility = View.INVISIBLE
        }
    }
    private fun setNavigationBarColor(colorResource: Int) {
        window.navigationBarColor = resources.getColor(colorResource, theme)
    }
}