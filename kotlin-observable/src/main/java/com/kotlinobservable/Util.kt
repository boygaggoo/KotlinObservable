package com.kotlinobservable

import android.os.Handler


fun Handler?.optPost(action:()->Unit){
    if(this!=null)post(action)
    else action()
}