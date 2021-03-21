package com.kamal.recyclerviewexp

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name
    private var treeObserverCalledOnce: Boolean = false

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById<RecyclerView>(R.id.id_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ListAdapter()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.d(TAG, "onScrolled")
                checkVisibleItems(recyclerView)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            if(!treeObserverCalledOnce) {
                // checks for visible items on activity loading
                checkVisibleItems(recyclerView)
                treeObserverCalledOnce = true
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
        checkVisibleItems(recyclerView)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        checkVisibleItems(recyclerView)
        viewModel.clearPrevMap()
    }

    private fun checkVisibleItems(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        var firstPosition = layoutManager.findFirstVisibleItemPosition()
        var lastPosition = layoutManager.findLastVisibleItemPosition()

        val globalVisibleRect = Rect()
        recyclerView.getGlobalVisibleRect(globalVisibleRect)
        val firstVisiblePercent = getVisibleHeightPercentage(layoutManager.findViewByPosition(firstPosition))
        if(firstVisiblePercent < 50){
            firstPosition++
        }

        val lastVisiblePercent = getVisibleHeightPercentage(layoutManager.findViewByPosition(firstPosition))
        if(lastVisiblePercent < 50){
            lastPosition--
        }

        viewModel.updateVisibleRange(firstPosition, lastPosition)
    }

    private fun getVisibleHeightPercentage(view: View?): Double {

        view?.let {
            val itemRect = Rect()
            val isParentViewEmpty = view.getLocalVisibleRect(itemRect)

            val visibleHeight = itemRect.height().toDouble()
            val height = view.measuredHeight

            val viewVisibleHeightPercentage = visibleHeight / height * 100

            if (isParentViewEmpty) {
                return viewVisibleHeightPercentage
            }
        }

        return 0.0
    }
}

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListVH>(){

    class ListVH(view: View) : RecyclerView.ViewHolder(view) {
        private var textView: TextView = view.findViewById<TextView>(R.id.item_label)
        fun bind(position: Int) {
            textView.text = "Index - $position"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListVH {
        return ListVH(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: ListVH, position: Int) {
        holder.bind(position)
    }

}