package com.example.notes_app_roomdb

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes_app_roomdb.adaptors.NoteAdapter
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.database.NoteDatabase
import com.example.notes_app_roomdb.databinding.ActivityMainBinding
import com.example.notes_app_roomdb.models.NoteViewModel

class MainActivity : AppCompatActivity(),NoteAdapter.NoteClickListener,NoteAdapter.DeleteIconChange {

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
        setNavigationBarColor(R.color.nav_color)

        setSupportActionBar(binding.toolbar)

        initUI()
        binding.searchBar.visibility = View.INVISIBLE
        binding.noteTV.visibility = View.VISIBLE
        binding.searchBTN.visibility = View.VISIBLE
        fun toggleSearchVisibility() {
            searchVisible = !searchVisible
            if (searchVisible) {
                binding.searchBar.visibility = View.VISIBLE
                binding.noteTV.visibility = View.INVISIBLE
                binding.searchBTN.visibility = View.INVISIBLE
                binding.searchBar.isIconified = false
            } else {
                binding.searchBar.visibility = View.INVISIBLE
                binding.noteTV.visibility = View.VISIBLE
                binding.searchBar.setQuery("", false)
                binding.searchBTN.visibility = View.VISIBLE
            }
        }

        binding.searchBTN.setOnClickListener {
            toggleSearchVisibility()

        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedNotes()
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
                    toggleSearchVisibility()
                }
            }
            false
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        binding.searchBar.setOnCloseListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
            binding.searchBar.clearFocus()
            binding.searchBar.setQuery("", false)
            toggleSearchVisibility()
            true
        }

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

        onLongPress(adapter.isLongClick)
    }

    private fun deleteSelectedNotes() {
        if(::adapter.isInitialized){
            val selectedNote = ArrayList(adapter.selectedNotes)
            viewModel.deleteSelectedNotes(selectedNote.map { it.id?:-1 })
            adapter.selectedNotes.clear()
            adapter.notifyDataSetChanged()
            adapter.isLongClick = false
            onLongPress(adapter.isLongClick)

        }
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
        adapter = NoteAdapter(this,this,this) {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            intent.putExtra("current_note", it)
            updateOrDeleteNote.launch(intent)
        }
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
                if (isDelete) {
                    viewModel.deleteNote(note)
                }else {
                    viewModel.updateNote(note)
                }
            }
        }
    override fun onItemClicked(note: Note) {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            intent.putExtra("current_note", note)
            updateOrDeleteNote.launch(intent)
    }

    override fun onLongPress(longPress: Boolean) {
        if(longPress){
            binding.deleteButton.visibility = View.VISIBLE
            binding.searchBTN.visibility = View.INVISIBLE
        }else{
            binding.deleteButton.visibility = View.INVISIBLE
            binding.searchBTN.visibility = View.VISIBLE
        }
    }

    private fun setNavigationBarColor(colorResource: Int) {
        window.navigationBarColor = resources.getColor(colorResource, theme)
    }


}