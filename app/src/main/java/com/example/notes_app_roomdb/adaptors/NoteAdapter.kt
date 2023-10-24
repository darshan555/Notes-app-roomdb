package com.example.notes_app_roomdb.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_app_roomdb.R
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.databinding.ListItemBinding
import java.util.Random

class NoteAdapter(
    private val context: Context,
    private val listener: NoteClickListener,
    private val deleteIconChangeCallback : DeleteIconChange,
    private val listener1 :(Note)->Unit
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val noteList = ArrayList<Note>()
    val selectedNotes = HashSet<Note>()
    var isLongClick = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = noteList[position]

        holder.binding.titleTV.text = item.title
        holder.binding.titleTV.isSelected = true
        holder.binding.contentTV.text = item.content
        holder.binding.dateTV.text = item.date
        holder.binding.dateTV.isSelected = true

        if (item.selected) {
            holder.binding.cardLayout.setBackgroundResource(
                R.drawable.note_selected_background
            )
        }else{
            holder.binding.cardLayout.setBackgroundResource(
                R.drawable.note_default_background
            )
        }


        holder.binding.cardLayout.setOnClickListener {
            if (isLongClick) {
                if (selectedNotes.contains(item)) {
                    selectedNotes.remove(item)
                    holder.binding.cardLayout.setBackgroundResource(
                        R.drawable.note_default_background
                    )
                    item.selected = false
                    if (selectedNotes.isEmpty()) {
                        isLongClick = false
                        deleteIconChangeCallback.onLongPress(isLongClick)
                    }
                    notifyDataSetChanged()
                } else {
                    item.selected = true
                    selectedNotes.add(item)
                    holder.binding.cardLayout.setBackgroundResource(
                        R.drawable.note_selected_background
                    )

                    notifyDataSetChanged()
                }
            } else {
                listener1.invoke(noteList[holder.adapterPosition])
            }
        }
        holder.binding.cardLayout.setOnLongClickListener {
            item.selected = true
            selectedNotes.add(item)
            isLongClick = true
            holder.binding.cardLayout.setBackgroundResource(
                R.drawable.note_selected_background
            )
            deleteIconChangeCallback.onLongPress(isLongClick)
            notifyDataSetChanged()
            true
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun updateList(newList: List<Note>) {
        noteList.clear()
        noteList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface NoteClickListener {
        fun onItemClicked(note: Note)
    }
    interface DeleteIconChange {
        fun onLongPress(longPress: Boolean)
    }
}
