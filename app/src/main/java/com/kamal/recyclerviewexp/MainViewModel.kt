package com.kamal.recyclerviewexp

import android.util.Log
import androidx.lifecycle.ViewModel

const val TWO_SECS: Long = 2*1000L

class MainViewModel : ViewModel(){

    private var prevList : MutableMap<Int, Long>? = null
    private val TAG : String = (MainViewModel::class.java).name

    fun updateVisibleRange(firstPosition: Int, lastPosition: Int) {
        var currList : MutableMap<Int, Long> = mutableMapOf()

        val currentTimeMillis = System.currentTimeMillis()
        for (i in firstPosition..lastPosition){
            currList[i] = currentTimeMillis
        }

        if(prevList == null){
            prevList = currList
            Log.d(TAG, "init map ${currList}")
            return
        }

        prevList?.forEach {
            if(currList.containsKey(it.key)){
                // overlapping items
                currList[it.key] = it.value
            } else if(currentTimeMillis - it.value >= TWO_SECS) {
                // send impression to server
                Log.d(TAG, "server ${it.key} ${currentTimeMillis - it.value}")
            }
        }

        prevList = currList
    }

    fun clearPrevMap() {
        prevList = null
    }
}
