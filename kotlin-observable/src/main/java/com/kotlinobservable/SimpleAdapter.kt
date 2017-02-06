package com.kotlinobservable

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class SimpleAdapter(private val count:()->Int, private val onCreateView:(ViewGroup, Int)-> View): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onBindView: SimpleAdapter.(RecyclerView.ViewHolder)->Unit={}
    private var itemViewType:(Int)->Int={super.getItemViewType(it)}
    override fun getItemViewType(position: Int): Int = itemViewType(position)
    override fun getItemCount(): Int = count()
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = onBindView(holder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=onCreateView(parent,viewType)
        return object : RecyclerView.ViewHolder(view){}
    }

    infix fun onBindView(onBindView: SimpleAdapter.(RecyclerView.ViewHolder)->Unit): SimpleAdapter {
        this.onBindView=onBindView
        notifyDataSetChanged()
        return this
    }

    infix fun itemViewType(itemViewType:(Int)->Int): SimpleAdapter {
        this.itemViewType=itemViewType
        notifyDataSetChanged()
        return this
    }
}