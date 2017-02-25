package com.kotlinobservable

import android.os.Handler
import com.kotlinobservable.ObservableList
import com.kotlinobservable.Observable
import java.util.*

open class ObservableArrayList<T>: ObservableList<T>, ArrayList<T> {
    @Transient private val listeners= hashSetOf<Observable.OnChangeListener<List<T>>>()
    @Transient private val handler: Handler?
    constructor(initialCapacity: Int) : super(initialCapacity){this.handler=null}
    constructor() : super(){this.handler=null}
    constructor(c: MutableCollection<out T>?) : super(c){this.handler=null}
    constructor(handler:Handler,initialCapacity: Int) : super(initialCapacity){this.handler=handler}
    constructor(handler:Handler) : super(){this.handler=handler}
    constructor(handler:Handler,c: MutableCollection<out T>?) : super(c){this.handler=handler}
    override var value: List<T>
        get() = this
        set(value) {
            super.clear()
            super.addAll(value)
            notifyChanged()
        }

    override fun add(element: T): Boolean {
        add(size,element)
        return true
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        notifyInserted(index, listOf(element))
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if(super.addAll(index, elements)){
            notifyInserted(index,elements.toList())
            return true
        }else return false
    }

    override fun addAll(elements: Collection<T>): Boolean = addAll(size,elements)

    override fun clear() {
        val elements= ArrayList(this)
        super.clear()
        notifyRemoved(0,elements)
    }

    override fun remove(element: T): Boolean {
        val index=indexOf(element)
        if(index<0)return false
        removeAt(index)
        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var changed=false
        elements.forEach { if(remove(it))changed=true }
        return changed
    }

    override fun removeAt(index: Int): T {
        val element=super.removeAt(index)
        notifyRemoved(index, listOf(element))
        return element
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        if(super.retainAll(elements)){
            notifyChanged()
            return true
        }else return false
    }

    override fun set(index: Int, element: T): T {
        val el=super.set(index, element)
        notifyChanged(index, listOf(el))
        return el
    }

    override fun addListener(listener: Observable.OnChangeListener<List<T>>) {
        listeners.add(listener)
    }

    override fun removeListener(listener: Observable.OnChangeListener<List<T>>) {
        listeners.remove(listener)
    }

    override fun notifyChanged() {
        listeners.forEach { handler.optPost { it.onChanged(this)} }
    }

    override fun notifyInserted(index: Int, elements:List<T>) {
        listeners.forEach {
            if(it is ObservableList.OnListChangedListener<T>) handler.optPost { it.onItemsInserted(this,index,elements) }
            else handler.optPost { it.onChanged(this) }
        }
    }

    override fun notifyRemoved(index: Int, elements:List<T>) {
        listeners.forEach {
            if(it is ObservableList.OnListChangedListener<T>) handler.optPost { it.onItemsRemoved(this,index,elements) }
            else it.onChanged(this)
        }
    }

    override fun notifyChanged(index: Int, elements:List<T>) {
        listeners.forEach {
            if(it is ObservableList.OnListChangedListener<T>) handler.optPost { it.onItemsChanged(this,index,elements) }
            else handler.optPost { it.onChanged(this) }
        }
    }
}