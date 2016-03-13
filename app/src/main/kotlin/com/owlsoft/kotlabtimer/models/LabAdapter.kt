package com.owlsoft.kotlabtimer.models

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.owlsoft.kotlabtimer.R
import kotlinx.android.synthetic.main.item_lab.view.*
import java.util.*
import java.util.concurrent.ScheduledFuture

/**
 * Created by mac on 26.02.16.
 */
class LabAdapter : RecyclerView.Adapter<LabAdapter.LabHolder>() {
    var data: List<Lab>? = null
        set(value) {
            field = value?.sortedBy { !it.isDeadline() }
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        if (data == null) return 0
        else return data?.size as Int
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LabHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_lab, parent, false)
        val labHolder: LabHolder = LabHolder(view)
        return labHolder
    }

    override fun onBindViewHolder(labHolder: LabHolder?, position: Int) {
        labHolder?.bindLab(data?.get(position) as Lab)
    }

    class LabHolder(view: View) : RecyclerView.ViewHolder(view) {
        val handler: Handler = Handler()
        var executor: ScheduledFuture<*>? = null

        fun bindLab(lab: Lab) {
            with(lab) {
                handler.postDelayed(object :Runnable{
                    override fun run():Unit {
                        if (isDeadline()) {
                            itemView.setBackgroundColor(itemView.context.resources.getColor(android.R.color.holo_red_dark))
                            itemView.dueto.text = "Deadline has come...:)"
                            executor?.cancel(true)
                        } else {
                            var diff = due_date.timeInMillis() - Calendar.getInstance().timeInMillis;

                            var diffSeconds = diff / 1000 % 60;
                            var diffMinutes = diff / (60 * 1000) % 60;
                            var diffHours = diff / (60 * 60 * 1000) % 24;
                            var diffDays = diff / (24 * 60 * 60 * 1000);

                            itemView.dueto.text = "${diffDays}d:${diffHours}h:${diffMinutes}m:${diffSeconds}s"
                            handler.postDelayed(this, 1000)
                        }
                    }
                }, 1000)

                if (isDeadline())
                    itemView.setBackgroundColor(itemView.context.resources.getColor(android.R.color.holo_red_dark))
                else
                    itemView.setBackgroundColor(itemView.context.resources.getColor(android.R.color.white))

                itemView.theme.text = theme
            }
        }
    }
}
