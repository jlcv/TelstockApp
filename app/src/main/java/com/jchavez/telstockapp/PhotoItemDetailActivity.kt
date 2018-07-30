package com.jchavez.telstockapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_photoitem_detail.*

/**
 * An activity representing a single PhotoItem detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [PhotoItemListActivity].
 */
class PhotoItemDetailActivity : AppCompatActivity() {

    private var itemId: Int = -1
    private lateinit var itemTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoitem_detail)
        detail_toolbar.title = resources.getString(R.string.title_photo_item_detail)
        setSupportActionBar(detail_toolbar)

        itemId = intent.getIntExtra(ARG_ITEM_ID, -1)
        itemTitle = intent.getStringExtra(ARG_ITEM_TITLE)

        fab.setOnClickListener { view ->
            val intent = Intent(view.context, PhotoItemEditActivity::class.java)
            intent.putExtra(ARG_MODE, ARG_EDIT_MODE)
            intent.putExtra(ARG_ITEM_TITLE, itemTitle)
            intent.putExtra(ARG_ITEM_ID, itemId)
            view.context.startActivity(intent)
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = PhotoItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_ID,
                            intent.getIntExtra(ARG_ITEM_ID, -1))
                    putString(ARG_ITEM_TITLE,
                            intent.getStringExtra(ARG_ITEM_TITLE))
                    putString(ARG_ITEM_URL,
                            intent.getStringExtra(ARG_ITEM_URL))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.photoitem_detail_container, fragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    navigateUpTo(Intent(this, PhotoItemListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    companion object {
        const val ARG_ITEM_TITLE = "item_title"
        const val ARG_EDIT_MODE = "edit_mode"
        const val ARG_MODE = "mode"
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_URL = "item_url"
    }
}
