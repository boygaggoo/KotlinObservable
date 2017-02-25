@file:Suppress("UNCHECKED_CAST")

package com.kotlinobservable

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import java.io.Serializable

interface ObservableList<T>:MutableList<T>, Observable<List<T>>,Serializable {
    fun notifyInserted(index: Int,elements:List<T>)
    fun notifyRemoved(index: Int,elements:List<T>)
    fun notifyChanged(index: Int,elements:List<T>)

    interface OnListChangedListener<T>: Observable.OnChangeListener<List<T>> {
        fun onItemsInserted(list: ObservableList<T>, index:Int, elements:List<T>)
        fun onItemsRemoved(list: ObservableList<T>, index:Int, elements:List<T>)
        fun onItemsChanged(list: ObservableList<T>, index:Int, elements:List<T>)
    }
}

fun <V:ViewPager,T> V.bind(fm:FragmentManager,list: List<T>,title:(Int)->CharSequence={""},width:(Int)->Float={1f},onCreateFragment:(Int)->android.support.v4.app.Fragment){
    adapter=object : FragmentStatePagerAdapter(fm){
        override fun getItem(position: Int): Fragment = onCreateFragment(position)
        override fun getCount(): Int = list.size
        override fun getItemPosition(`object`: Any?): Int = PagerAdapter.POSITION_NONE
        override fun getPageTitle(position: Int): CharSequence = title(position)
        override fun getPageWidth(position: Int): Float = width(position)
    }
    if(list is Observable<*>){
        val listener=object: ObservableList.OnListChangedListener<T> {
            override fun onItemsChanged(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyDataSetChanged()
            override fun onItemsRemoved(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyDataSetChanged()
            override fun onItemsInserted(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyDataSetChanged()
            override fun onChanged(value: List<T>) = adapter.notifyDataSetChanged()
        }
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View?) = (list as Observable<List<T>>).addListener(listener)
            override fun onViewDetachedFromWindow(p0: View?) = (list as Observable<List<T>>).removeListener(listener)
        })
        if(ViewCompat.isAttachedToWindow(this))(list as Observable<List<T>>).addListener(listener)
    }
}

fun <V:AdapterView<*>,T> V.bind(list: List<T>,onCreateView: (ViewGroup, Int) -> View):SimpleAdapter{
    val adapter=SimpleAdapter({list.size},onCreateView)
    adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
        override fun onChanged() { (this@bind.adapter as? BaseAdapter?)?.notifyDataSetChanged() }
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = onChanged()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) = onChanged()
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = onChanged()
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = onChanged()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = onChanged()
    })
    this.adapter=object : BaseAdapter(){
        override fun getView(p0: Int, p1: View?, p2: ViewGroup): View {
            val holder=(p1?.getTag(R.id.view_holder) as? RecyclerView.ViewHolder?)?:adapter.createViewHolder(p2,adapter.getItemViewType(p0))
            adapter.bindViewHolder(holder,p0)
            holder.itemView?.setTag(R.id.view_holder,holder)
            return holder.itemView
        }
        override fun getItem(p0: Int): T = list[p0]
        override fun getItemId(p0: Int): Long = p0.toLong()
        override fun getCount(): Int = list.size
    }
    if(list is Observable<*>){
        val listener=object: ObservableList.OnListChangedListener<T> {
            override fun onItemsChanged(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyItemRangeChanged(index,elements.size)
            override fun onItemsRemoved(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyItemRangeRemoved(index,elements.size)
            override fun onItemsInserted(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyItemRangeInserted(index,elements.size)
            override fun onChanged(value: List<T>) = adapter.notifyDataSetChanged()
        }
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View?) = (list as Observable<List<T>>).addListener(listener)
            override fun onViewDetachedFromWindow(p0: View?) = (list as Observable<List<T>>).removeListener(listener)
        })
        if(ViewCompat.isAttachedToWindow(this))(list as Observable<List<T>>).addListener(listener)
    }
    return adapter
}

fun <V: RecyclerView,T> V.bind(list: List<T>, onCreateView:(ViewGroup, Int)-> View): SimpleAdapter {
    val adapter= SimpleAdapter({list.size},onCreateView)
    this.adapter=adapter
    if(list is Observable<*>){
        val listener=object: ObservableList.OnListChangedListener<T> {
            override fun onItemsChanged(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyItemRangeChanged(index,elements.size)
            override fun onItemsRemoved(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyItemRangeRemoved(index,elements.size)
            override fun onItemsInserted(list: ObservableList<T>, index: Int, elements: List<T>) = adapter.notifyItemRangeInserted(index,elements.size)
            override fun onChanged(value: List<T>) = adapter.notifyDataSetChanged()
        }
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View?) = (list as Observable<List<T>>).addListener(listener)
            override fun onViewDetachedFromWindow(p0: View?) = (list as Observable<List<T>>).removeListener(listener)
        })
        if(ViewCompat.isAttachedToWindow(this))(list as Observable<List<T>>).addListener(listener)
    }
    return adapter
}