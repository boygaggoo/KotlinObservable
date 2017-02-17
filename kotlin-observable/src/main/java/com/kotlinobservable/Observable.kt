package com.kotlinobservable

import android.view.View

interface Observable<T>{
    fun addListener(listener: OnChangeListener<T>)
    fun removeListener(listener: OnChangeListener<T>)
    fun notifyChanged()
    var value:T

    interface OnChangeListener<in T>{
        fun onChanged(value:T)
    }
}

fun <V: View,T1,T2,T3,T4> V.bind(observable1: Observable<T1>, observable2: Observable<T2>, observable3: Observable<T3>, observable4: Observable<T4>, onChange: V.(T1, T2, T3, T4) -> Unit):V{
    bind(observable1){onChange(it,observable2.value,observable3.value,observable4.value)}
    bind(observable2){onChange(observable1.value,it,observable3.value,observable4.value)}
    bind(observable3){onChange(observable1.value,observable2.value,it,observable4.value)}
    bind(observable4){onChange(observable1.value,observable2.value,observable3.value,it)}
    return this
}

fun <V: View,T1,T2,T3> V.bind(observable1: Observable<T1>, observable2: Observable<T2>, observable3: Observable<T3>, onChange: V.(T1, T2, T3) -> Unit):V{
    bind(observable1){onChange(it,observable2.value,observable3.value)}
    bind(observable2){onChange(observable1.value,it,observable3.value)}
    bind(observable3){onChange(observable1.value,observable2.value,it)}
    return this
}

fun <V: View,T1,T2> V.bind(observable1: Observable<T1>, observable2: Observable<T2>, onChange: V.(T1, T2) -> Unit):V{
    bind(observable1){onChange(it,observable2.value)}
    bind(observable2){onChange(observable1.value,it)}
    return this
}

fun <V: View,T> V.bind(observable: Observable<T>, onChange:V.(T)->Unit):V{
    val listener=object : Observable.OnChangeListener<T> {
        override fun onChanged(value: T) = onChange(value)
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(p0: View?) {
            observable.addListener(listener)
            listener.onChanged(observable.value)
        }
        override fun onViewDetachedFromWindow(p0: View?) = observable.removeListener(listener)
    })
    observable.addListener(listener)
    listener.onChanged(observable.value)
    return this
}
