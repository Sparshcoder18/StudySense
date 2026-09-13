package com.studysense.app.ui.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studysense.app.data.entity.Subject
import com.studysense.app.databinding.ItemSubjectBinding

class SubjectAdapter(
    private val onClick: (Subject) -> Unit,
    private val onDeleteClick: (Subject) -> Unit
) : ListAdapter<Subject, SubjectAdapter.SubjectViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubjectViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) {
            binding.textSubjectName.text = subject.name
            binding.textSubjectTeacher.text = subject.teacher ?: ""
            binding.textSubjectTeacher.visibility =
                if (subject.teacher.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE

            binding.root.setOnClickListener { onClick(subject) }
            binding.buttonDeleteSubject.setOnClickListener { onDeleteClick(subject) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Subject>() {
            override fun areItemsTheSame(oldItem: Subject, newItem: Subject) =
                oldItem.subjectId == newItem.subjectId

            override fun areContentsTheSame(oldItem: Subject, newItem: Subject) =
                oldItem == newItem
        }
    }
}
