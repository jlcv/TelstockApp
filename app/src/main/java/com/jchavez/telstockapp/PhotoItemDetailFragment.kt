package com.jchavez.telstockapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photoitem_detail.view.*

/**
 * A fragment representing a single PhotoItem detail screen.
 * This fragment is either contained in a [PhotoItemListActivity]
 * in two-pane mode (on tablets) or a [PhotoItemDetailActivity]
 * on handsets.
 */
class PhotoItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private lateinit var itemUrl: String
    private lateinit var itemTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_URL)) {
                itemUrl = it.getString(ARG_ITEM_URL)
                itemTitle = it.getString(ARG_ITEM_TITLE)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.photoitem_detail, container, false)
        rootView.detailTextView.text = itemTitle
        Picasso.get().load(itemUrl).placeholder(android.R.drawable.ic_menu_gallery).into(rootView.detailImageView)

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_TITLE = "item_title"
        const val ARG_ITEM_URL = "item_url"
    }
}
