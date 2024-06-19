package com.capstone.chilifit.helper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.chilifit.data.local.entity.ResultEntity
import com.capstone.chilifit.databinding.RvHistoryBinding
import com.capstone.chilifit.ui.main.history.HistoryViewModel

class HistoryAdapter (private val viewModel: HistoryViewModel): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var results = emptyList<ResultEntity>()

    inner class HistoryViewHolder(private val binding: RvHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resultEntity: ResultEntity) {
            binding.apply {
                classificationHistory.text = resultEntity.classificationResult
                Glide.with(itemView)
                    .load(resultEntity.classificationImagePath)
                    .centerCrop()
                    .into(imageHistory)
                deleteHistory.setOnClickListener {
                    showDeleteConfirmationDialog(itemView.context, resultEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            RvHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int {
        return results.size
    }

    fun setData(results: List<ResultEntity>) {
        this.results = results
        notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(context: Context, resultEntity: ResultEntity) {
        AlertDialog.Builder(context)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteResult(resultEntity)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}