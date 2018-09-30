package br.ufpe.cin.if710.rss

import android.arch.persistence.room.Room
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.itemlista.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.uiThread
import java.util.*


class MyAdapter(private var dataset: List<Entry> = Collections.emptyList()) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // provide a reference to the views for each data item
    class MyViewHolder(val v: View) : RecyclerView.ViewHolder(v)


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.itemlista, parent, false)
        return MyViewHolder(v)
    }

    // replace the contents of a view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // get element from dataset at this position and
        // replace the contents of the view with that element
        holder.v.apply {
            item_titulo.apply {
                text = dataset[position].title
                onClick {
                    // open entry url on browser
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(dataset[position].link)
                    startActivity(getContext(), i, null)

                    // set entry as read when clicked
                    doAsync {
                        val db = Room.databaseBuilder(getContext(), AppDatabase::class.java, "database").build()
                        db.entryDao().update(dataset[position].apply {
                            isRead = true
                        })

                        // update recycler view after update database
                        val dataset = db.entryDao().items.asReversed()
                        uiThread {
                            setDataset(dataset)
                        }
                    }
                }
            }
            item_data.text = dataset[position].datetime
        }
    }

    // return the size of your dataset
    override fun getItemCount() = dataset.size

    fun setDataset(dataset: List<Entry>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }
}
