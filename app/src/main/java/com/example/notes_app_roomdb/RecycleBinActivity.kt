package com.example.notes_app_roomdb

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes_app_roomdb.adaptors.NoteAdapter
import com.example.notes_app_roomdb.adaptors.RecBinAdapter
import com.example.notes_app_roomdb.databinding.ActivityRecycleBinBinding
import com.example.notes_app_roomdb.databinding.CustomDeleteDialogBinding
import com.example.notes_app_roomdb.models.NoteViewModel

class RecycleBinActivity : AppCompatActivity(), NoteAdapter.DeleteIconChange {

    lateinit var binding: ActivityRecycleBinBinding
    private lateinit var adapter: RecBinAdapter
    private lateinit var viewModel: NoteViewModel
    private lateinit var dbbinding: CustomDeleteDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbbinding = CustomDeleteDialogBinding.inflate(layoutInflater)
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
            val message :String? = "Are you sure want to delete permanent?"
            showCustomDialogDelete(message)
        }

        var isSelectAll = false

        binding.selectAllBTN.setOnClickListener {
            if (isSelectAll) {
                adapter.selectedNotes.clear()
                onLongPress(false)
            } else {
                viewModel.deletedNote.value?.let { deletedNotesList ->
                    adapter.selectedNotes.addAll(deletedNotesList)
                }
            }
            adapter.notifyDataSetChanged()
            isSelectAll = !isSelectAll
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

    private fun showCustomDialogDelete(message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        // Inflate the custom dialog layout
        val dialogBinding = CustomDeleteDialogBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root

        // Set the content view of the dialog
        dialog.setContentView(dialogView)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Now, you can access views from dialogBinding
        dialogBinding.messageTV.text = message

        dialogBinding.yesBTN.setOnClickListener {
            if(::adapter.isInitialized){
                val selectedNote = ArrayList(adapter.selectedNotes)
                viewModel.deleteSelectedNotes(selectedNote.map { it.id?:-1 })
                adapter.selectedNotes.clear()
                adapter.notifyDataSetChanged()
                adapter.isLongClick = false
                onLongPress(false)
            }
            dialog.dismiss()
        }

        dialogBinding.noBTN.setOnClickListener {
            adapter.selectedNotes.clear()
            adapter.notifyDataSetChanged()
            onLongPress(false)
            dialog.dismiss()
        }

        dialog.show()
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
            binding.RecTV.visibility = View.INVISIBLE
            binding.selectAllBTN.visibility = View.VISIBLE
        }else{
            binding.deletePButton.visibility = View.INVISIBLE
            binding.restoreButton.visibility = View.INVISIBLE
            binding.RecTV.visibility = View.VISIBLE
            binding.selectAllBTN.visibility = View.INVISIBLE
        }
    }
    private fun setNavigationBarColor(colorResource: Int) {
        window.navigationBarColor = resources.getColor(colorResource, theme)
    }
}