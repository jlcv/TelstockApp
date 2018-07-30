package com.jchavez.telstockapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.jchavez.telstockapp.models.PhotoItem
import com.jchavez.telstockapp.retrofit.ApiService
import com.jchavez.telstockapp.retrofit.PhotosResult
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photoitem_list.*
import kotlinx.android.synthetic.main.photoitem_list_content.view.*
import kotlinx.android.synthetic.main.photoitem_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [PhotoItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class PhotoItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private val apiService = ApiService.create()
    private val tagPhotos = "PHOTOS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoitem_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val intent = Intent(view.context, PhotoItemEditActivity::class.java)
            intent.putExtra(ARG_MODE, ARG_ADD_MODE)
            view.context.startActivity(intent)
        }

        if (photoitem_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        getPhotos()
    }

    private fun getPhotos() {
        apiService.photos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::successPhotos, this::handleErrors)
    }

    private fun successPhotos(result: List<PhotoItem>) {
        if (result.count() == 0) {
            Log.d(tagPhotos, "No photos")
        } else {
            Log.d(tagPhotos, "Got results!")
            Log.d(tagPhotos, result.toString())
            result.forEach {
                Log.d(tagPhotos, it.title)
            }
            setupRecyclerView(photoitem_list, result)
        }
    }

    private fun handleErrors(error: Throwable?) {
        val code = error.toString()
        Log.e(TAG_ERROR, code)
        when (code) {
            "401" -> print(code)
            else -> print(code)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, result: List<PhotoItem>) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, result, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: PhotoItemListActivity,
                                        private val values: List<PhotoItem>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as PhotoItem
                if (twoPane) {
                    val fragment = PhotoItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt(ARG_ITEM_ID, item.id)
                            putString(ARG_ITEM_TITLE, item.title)
                            putString(ARG_ITEM_URL, item.url)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.photoitem_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, PhotoItemDetailActivity::class.java).apply {
                        this.putExtra(ARG_ITEM_ID, item.id)
                        this.putExtra(ARG_ITEM_TITLE, item.title)
                        this.putExtra(ARG_ITEM_URL, item.url)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photoitem_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.titleTextView.text = item.title
            Picasso.get().load(item.thumbnailUrl).placeholder(android.R.drawable.ic_menu_gallery).into(holder.thumbnailImageView)

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumbnailImageView: ImageView = view.thumbnailImageView
            val titleTextView: TextView = view.titleTextView
        }
    }

    companion object {
        const val ARG_ITEM_TITLE = "item_title"
        const val ARG_ADD_MODE = "add_mode"
        const val ARG_MODE = "mode"
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_URL = "item_url"
        const val TAG_ERROR = "ERROR"
    }
}
