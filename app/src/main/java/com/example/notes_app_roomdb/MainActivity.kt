package com.example.notes_app_roomdb

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes_app_roomdb.adaptors.NoteAdapter
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.database.NoteDatabase
import com.example.notes_app_roomdb.database.SortingPreference
import com.example.notes_app_roomdb.databinding.ActivityMainBinding
import com.example.notes_app_roomdb.databinding.CustomDeleteDialogBinding
import com.example.notes_app_roomdb.databinding.CustomPopupMenuBinding
import com.example.notes_app_roomdb.models.NoteViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),NoteAdapter.NoteClickListener,NoteAdapter.DeleteIconChange, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    private lateinit var noteList: List<Note>
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter
    private var searchVisible = false
    private lateinit var popupWindow: PopupWindow
    private lateinit var cpbinding: CustomPopupMenuBinding
    private lateinit var dbbinding: CustomDeleteDialogBinding

    private var currentSortingPreference: SortingPreference = SortingPreference.DATE_DESCENDING


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        cpbinding = CustomPopupMenuBinding.inflate(layoutInflater)
        dbbinding = CustomDeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor(R.color.nav_color)

        binding.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        initUI()
        binding.navView.setBackgroundColor(ContextCompat.getColor(this,R.color.nav_color))

        binding.drawerBTN.setOnClickListener{

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.searchBar.visibility = View.INVISIBLE
        binding.noteTV.visibility = View.VISIBLE
        binding.searchBTN.visibility = View.VISIBLE


        binding.searchBTN.setOnClickListener {
            toggleSearchVisibility()
        }

        binding.deleteButton.setOnClickListener {
            val message :String? = "Are you sure want to delete?"
            showCustomDialogDelete(message)
        }

        popupWindow = PopupWindow(
            cpbinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val popup = PopupWindow(this)
        popup.setBackgroundDrawable( ColorDrawable(
            Color.TRANSPARENT)
        )
        popup.contentView = cpbinding.root
        popup.isOutsideTouchable = true
        popup.isFocusable = true


        binding.filterButton.setOnClickListener{
            popup.showAsDropDown(binding.filterButton,25,50,Gravity.RIGHT)
        }

        cpbinding.rB.isChecked = true
        cpbinding.radioButtonOption1.setOnClickListener{
            cpbinding.rA.isChecked = true
            cpbinding.rB.isChecked = false
            cpbinding.rC.isChecked = false
            cpbinding.rD.isChecked = false
            popup.dismiss()
            sortByDateAscending()
        }

        cpbinding.radioButtonOption2.setOnClickListener{
            cpbinding.rA.isChecked = false
            cpbinding.rB.isChecked = true
            cpbinding.rC.isChecked = false
            cpbinding.rD.isChecked = false
            popup.dismiss()
            sortByDateDescending()
        }
        cpbinding.radioButtonOption3.setOnClickListener{
            cpbinding.rA.isChecked = false
            cpbinding.rB.isChecked = false
            cpbinding.rC.isChecked = true
            cpbinding.rD.isChecked = false
            popup.dismiss()
            sortByTitle()
        }
        cpbinding.radioButtonOption4.setOnClickListener{
            cpbinding.rA.isChecked = false
            cpbinding.rB.isChecked = false
            cpbinding.rC.isChecked = false
            cpbinding.rD.isChecked = true
            popup.dismiss()
            sortByContent()
        }


        binding.drawerLayout.setOnTouchListener { _, event ->
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

        viewModel.filterNote.observe(this){
            list-> list?.let {
                noteList = list
                adapter.updateList(noteList)
                applySorting()
        }

        }
        database = NoteDatabase.getDatabase(this)

        uiViewCheck()

        onLongPress(adapter.isLongClick)
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
            if (::adapter.isInitialized) {
                val selectedNote = ArrayList(adapter.selectedNotes)
                viewModel.temporaryDelete(selectedNote.map { it.id ?: -1 })
                adapter.selectedNotes.clear()
                adapter.notifyDataSetChanged()
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

    private fun toggleSearchVisibility() {
        searchVisible = !searchVisible
        if (searchVisible) {
            binding.searchBar.visibility = View.VISIBLE
            binding.filterButton.visibility = View.INVISIBLE
            binding.noteTV.visibility = View.INVISIBLE
            binding.searchBTN.visibility = View.INVISIBLE
            binding.searchBar.isIconified = false
        } else {
            binding.searchBar.visibility = View.INVISIBLE
            binding.filterButton.visibility = View.VISIBLE
            binding.noteTV.visibility = View.VISIBLE
            binding.searchBar.setQuery("", false)
            binding.searchBTN.visibility = View.VISIBLE
        }
    }
    private fun sortByDateAscending() {
        currentSortingPreference = SortingPreference.DATE_ASCENDING
        applySorting()
    }

    private fun sortByDateDescending() {
        currentSortingPreference = SortingPreference.DATE_DESCENDING
        applySorting()
    }

    private fun sortByTitle() {
        currentSortingPreference = SortingPreference.TITLE
        applySorting()
    }

    private fun sortByContent() {
        currentSortingPreference = SortingPreference.CONTENT
        applySorting()
    }
    private fun applySorting() {
        noteList = when (currentSortingPreference) {
            SortingPreference.DATE_ASCENDING -> noteList.sortedBy { it.updatedDate }
            SortingPreference.DATE_DESCENDING -> noteList.sortedByDescending { it.updatedDate }
            SortingPreference.TITLE -> noteList.sortedBy { it.title }
            SortingPreference.CONTENT -> noteList.sortedBy { it.content }
        }
        adapter.updateList(noteList)
    }

    private fun filterNotes(query: String?) {
        viewModel.filterNote.observe(this) { list ->
            list?.let {
                if (query.isNullOrBlank()) {
                    adapter.updateList(list)
                } else {
                    val filteredList = list.filter { note ->
                        note.content?.contains(query, ignoreCase = true) == true ||
                                note.title?.contains(query, ignoreCase = true) == true
                    }
                    adapter.updateList(filteredList)
                    binding.filterButton.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initUI() {
        binding.listRecView.setHasFixedSize(true)
        binding.listRecView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = NoteAdapter(this) {
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
            adapter.selectedNotes.clear()
            onLongPress(false)
            uiViewCheck()
            adapter.isLongClick = false
            adapter.notifyDataSetChanged()
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
            binding.searchBTN.visibility = View.INVISIBLE
            binding.deleteButton.visibility = View.VISIBLE
        }else{
            binding.deleteButton.visibility = View.INVISIBLE
            if(searchVisible){
                binding.searchBTN.visibility = View.INVISIBLE
            }else{
                binding.searchBTN.visibility = View.VISIBLE
            }
        }
    }

    private fun setNavigationBarColor(colorResource: Int) {
        window.navigationBarColor = resources.getColor(colorResource, theme)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home->{
                item.isCheckable =false
                Toast.makeText(this, "Home Activity", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_about->{
                item.isCheckable =false
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_recycle_bin->{
                item.isCheckable =false
                startActivity(Intent(this, RecycleBinActivity::class.java))
                adapter.selectedNotes.clear()
                onLongPress(false)
                adapter.isLongClick = false
                adapter.notifyDataSetChanged()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
     private fun uiViewCheck(){
         viewModel.filterNote.observe(this) { notes ->
             if (notes.isNullOrEmpty()) {
                 binding.noNoteTV.visibility = View.VISIBLE
                 binding.noNoteIMG.visibility = View.VISIBLE
                 binding.filterButton.visibility = View.INVISIBLE
                 binding.searchBTN.visibility = View.INVISIBLE
             } else {
                 binding.noNoteTV.visibility = View.INVISIBLE
                 binding.noNoteIMG.visibility = View.INVISIBLE
                 binding.filterButton.visibility = View.VISIBLE
                 binding.searchBTN.visibility = View.VISIBLE
             }
         }
     }

}