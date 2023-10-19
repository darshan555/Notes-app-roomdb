package com.example.notes_app_roomdb.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_app_roomdb.R
import com.example.notes_app_roomdb.database.Note

class NoteAdapter(private val context: Context, val listener: NoteClickListener,  val longClickListener: OnItemLongClickListener):
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val noteList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {

        return NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }
    override fun onBindViewHolder(holder: NoteAdapter.NoteViewHolder, position: Int) {
        val item = noteList[position]
        holder.title.text = item.title
        holder.title.isSelected = true
        holder.content.text = item.content
        holder.date.text = item.date
        holder.date.isSelected = true
        holder.note_layout.setOnClickListener {
            listener.onItemClicked(noteList[holder.adapterPosition])
        }

        holder.note_layout.setOnLongClickListener {
            longClickListener.onItemLongClicked(noteList[holder.adapterPosition])
            true
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun updateList(newList: List<Note>){
        noteList.clear()
        noteList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val note_layout = itemView.findViewById<CardView>(R.id.card_layout)
        val title = itemView.findViewById<TextView>(R.id.titleTV)
        val content = itemView.findViewById<TextView>(R.id.contentTV)
        val date = itemView.findViewById<TextView>(R.id.dateTV)
    }

    interface NoteClickListener {
        fun onItemClicked(note: Note)
    }
    interface OnItemLongClickListener {
        fun onItemLongClicked(note: Note)
    }
}