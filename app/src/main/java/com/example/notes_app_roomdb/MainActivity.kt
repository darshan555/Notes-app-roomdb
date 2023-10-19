package com.example.notes_app_roomdb

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes_app_roomdb.adaptors.NoteAdapter
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.database.NoteDatabase
import com.example.notes_app_roomdb.databinding.ActivityMainBinding
import com.example.notes_app_roomdb.models.NoteViewModel

class MainActivity : AppCompatActivity(),NoteAdapter.NoteClickListener,NoteAdapter.OnItemLongClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter
    private var searchVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        binding.searchBar.visibility = View.INVISIBLE
        binding.searchBTN.visibility = View.VISIBLE

        binding.searchBTN.setOnClickListener {
            searchVisible = !searchVisible
            if (searchVisible) {
                binding.searchBar.visibility = View.VISIBLE
                binding.searchBTN.visibility = View.INVISIBLE
                binding.searchBar.isIconified = false
            } else {
                binding.searchBar.visibility = View.INVISIBLE
                binding.searchBTN.visibility = View.VISIBLE
            }
        }

        binding.constraintLayout.setOnTouchListener { _, event ->
            if (searchVisible && event.action == MotionEvent.ACTION_DOWN) {
                val location = IntArray(2)
                binding.searchBar.getLocationOnScreen(location)
                val x = event.rawX
                val y = event.rawY
                val searchBarX = location[0]
                val searchBarY = location[1]
                if (x < searchBarX || x > searchBarX + binding.searchBar.width || y < searchBarY || y > searchBarY + binding.searchBar.height) {
                    binding.searchBar.visibility = View.INVISIBLE
                    binding.searchBar.setQuery("", false)
                    binding.searchBTN.visibility = View.VISIBLE
                    searchVisible = false
                }
            }
            false
        }


        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

        viewModel.allNote.observe(this){
            list-> list?.let {
                adapter.updateList(list)
        }
        }
        database = NoteDatabase.getDatabase(this)
    }

    private fun filterNotes(query: String?) {
        viewModel.allNote.observe(this) { list ->
            list?.let {
                if (query.isNullOrBlank()) {
                    adapter.updateList(list)
                } else {
                    val filteredList = list.filter { note ->
                        note.content?.contains(query, ignoreCase = true) == true ||
                                note.title?.contains(query, ignoreCase = true) == true
                    }
                    adapter.updateList(filteredList)
                }
            }

        }
    }

    private fun initUI() {
        binding.listRecView.setHasFixedSize(true)
        binding.listRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NoteAdapter(this,this,this)
        binding.listRecView.adapter = adapter

        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
                if(result.resultCode == Activity.RESULT_OK){
                    val note = result.data?.getSerializableExtra("note") as? Note
                    if(note!=null){
                        viewModel.insertNote(note)
                    }
                }
            }
        binding.addNoteBTN.setOnClickListener{
            val intent = Intent(this, AddNoteActivity::class.java)
            getContent.launch(intent)
        }
    }
    private val updateOrDeleteNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val note = result.data?.getSerializableExtra("note") as Note
                val isDelete = result.data?.getBooleanExtra("delete_note", false) as Boolean
                if (!isDelete) {
                    viewModel.updateNote(note)
                }else if(isDelete){
                    viewModel.deleteNote(note)
                }
            }
        }
    override fun onItemClicked(note: Note) {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            intent.putExtra("current_note", note)
            updateOrDeleteNote.launch(intent)
    }

    override fun onItemLongClicked(note: Note) {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder
            .setMessage("Are you sure you want to delete this note")
            .setTitle("Delete")
            .setPositiveButton("Yes"){dialog,which ->
                viewModel.deleteNote(note)
                Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}