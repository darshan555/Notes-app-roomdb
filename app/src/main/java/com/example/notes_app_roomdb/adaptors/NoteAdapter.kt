package com.example.notes_app_roomdb.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_app_roomdb.R
import com.example.notes_app_roomdb.database.Note
import com.example.notes_app_roomdb.databinding.ListItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class NoteAdapter(
    private val deleteIconChangeCallback: DeleteIconChange,
    private val listener1: (Note) -> Unit
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val backgroundColors = arrayOf(
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.color5,
        R.color.color6
    )
    private val noteList = ArrayList<Note>()
    val selectedNotes = HashSet<Note>()
    var isLongClick = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = noteList[position]
        val colorIndex = item.color - 1

        //Show Data
        holder.binding.cardLayout.setCardBackgroundColor(holder.itemView.getResources().getColor(backgroundColors[colorIndex], null))
        holder.binding.titleTV.text = item.title
        holder.binding.titleTV.isSelected = true
        holder.binding.contentTV.text = item.content
        holder.binding.dateTV.isSelected = true

        // Date-timing formatting
        val formatter = SimpleDateFormat("hh:mm a")
        val today = Calendar.getInstance()
        val updatedDate = Calendar.getInstance()
        val datePattern = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a")
        val parsedDate = datePattern.parse(item.updatedDate)
        updatedDate.time = parsedDate

        val dateTextView = holder.binding.dateTV

        if (today.get(Calendar.YEAR) == updatedDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == updatedDate.get(Calendar.DAY_OF_YEAR)
        ) {
            val formattedDate = formatter.format(parsedDate)
            dateTextView.text = formattedDate
        } else {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy")
            val formattedDate = dateFormat.format(parsedDate)
            dateTextView.text = formattedDate
        }

        //Check visibility

        if (selectedNotes.contains(item)) {
            holder.binding.checkBTN.visibility = View.VISIBLE
        } else {
            holder.binding.checkBTN.visibility = View.INVISIBLE
        }

        //Single click
        holder.binding.cardLayout.setOnClickListener {
            if (isLongClick) {
                if (selectedNotes.contains(item)) {
                    selectedNotes.remove(item)
                    holder.binding.checkBTN.visibility = View.INVISIBLE
                    if (selectedNotes.isEmpty()) {
                        isLongClick = false
                        deleteIconChangeCallback.onLongPress(isLongClick)
                    }
                    notifyDataSetChanged()
                } else {
                    selectedNotes.add(item)
                    holder.binding.checkBTN.visibility = View.VISIBLE
                    notifyDataSetChanged()
                }
            } else {
                listener1.invoke(noteList[holder.adapterPosition])
            }
        }
        holder.binding.cardLayout.setOnLongClickListener {
            selectedNotes.add(item)
            isLongClick = true
            holder.binding.checkBTN.visibility = View.VISIBLE
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
