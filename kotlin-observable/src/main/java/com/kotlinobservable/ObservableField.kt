package com.kotlinobservable

import kotlin.properties.Delegates

class ObservableField<T>(value:T,onChange:((T)->Unit)?=null) : Observable<T> {
    private val listeners= hashSetOf<Observable.OnChangeListener<T>>()
    override var value by Delegates.observable(value){ prop, old, new->
        if(old==new)return@observable
        notifyChanged()
    }

    init {
        if(onChange!=null){
            listeners.add(object : Observable.OnChangeListener<T> {
                override fun onChanged(value: T) = onChange(value)
            })
        }
    }

    override fun addListener(listener: Observable.OnChangeListener<T>) {
        listeners.add(listener)
    }

    override fun removeListener(listener: Observable.OnChangeListener<T>) {
        listeners.remove(listener)
    }

    override fun notifyChanged() {
        listeners.forEach { it.onChanged(value) }
    }
}