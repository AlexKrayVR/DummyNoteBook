package alex.com.notebook.ui.notes

import alex.com.notebook.data.Note
import alex.com.notebook.databinding.ItemNoteBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Note, NotesAdapter.TaskViewHolder>(DiffCallback()) {

    var formatterDate = SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault())

    interface OnItemClickListener {
        fun onItemClick(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val note = getItem(position)
                    listener.onItemClick(note)
                }
            }
        }

        fun bind(note: Note) {
            binding.apply {
                title.text = note.title
                description.text = note.description
                cardView.setCardBackgroundColor(note.color)

                val currentCalendar = GregorianCalendar.getInstance()
                currentCalendar.timeInMillis = note.createdTime

                data.text = formatterDate.format(currentCalendar.time)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}