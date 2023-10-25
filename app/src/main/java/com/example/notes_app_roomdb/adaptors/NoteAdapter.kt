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
        val colorIndex = position % backgroundColors.size

        holder.binding.cardLayout.setCardBackgroundColor(holder.itemView.getResources().getColor(backgroundColors[colorIndex],null))
        holder.binding.titleTV.text = item.title
        holder.binding.titleTV.isSelected = true
        holder.binding.contentTV.text = item.content
        holder.binding.dateTV.text = item.date
        holder.binding.dateTV.isSelected = true

        if (item.selected) {
            holder.binding.checkBTN.visibility = View.VISIBLE
        }else{
            holder.binding.checkBTN.visibility = View.INVISIBLE
        }

        holder.binding.cardLayout.setOnClickListener {
            if (isLongClick) {
                if (selectedNotes.contains(item)) {
                    selectedNotes.remove(item)
                    holder.binding.checkBTN.visibility = View.INVISIBLE
                    item.selected = false
                    if (selectedNotes.isEmpty()) {
                        isLongClick = false
                        deleteIconChangeCallback.onLongPress(isLongClick)
                    }
                    notifyDataSetChanged()
                } else {
                    item.selected = true
                    selectedNotes.add(item)
                    holder.binding.checkBTN.visibility = View.VISIBLE

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
            holder.binding.checkBTN.visibility = View.VISIBLE
            deleteIconChangeCallback.onLongPress(isLongClick)
            notifyDataSetChanged()
            true
        }
    }

   /* private fun getRandomColor(): Int {
        val colorCode = listOf(
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color6
        )

        val random = Random()
        val randomColor = random.nextInt(colorCode.size)
        return colorCode[randomColor]
    }*/

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
