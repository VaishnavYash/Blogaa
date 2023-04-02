package yash.com.example.blogaa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text
import yash.com.example.blogaa.R
import yash.com.example.blogaa.PostModel

class PostAdapter(
    private var context: Context,
    private var postModelList: List<PostModel>,
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view  = LayoutInflater.from(context).inflate(R.layout.blog_row, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image)
        var postTitle: TextView = itemView.findViewById(R.id.post_title)
        var postDescription: TextView = itemView.findViewById(R.id.post_desc)
        var userName: TextView = itemView.findViewById(R.id.post_user)

        var likeBtn : ImageButton = itemView.findViewById(R.id.likeBtn)
        var likeScore : TextView = itemView.findViewById(R.id.post_score)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var currentPos  = postModelList[position]
        val viewHolder = holder as? ViewHolder
        viewHolder!!.postTitle.text = currentPos.pTitle
        viewHolder.postDescription.text = currentPos.pDesc
        viewHolder.userName.text = "-"+currentPos.pName


        var imageString: String? = currentPos.pImage
        Glide.with(context).load(imageString).into(viewHolder.postImage)

        viewHolder.likeScore.text = currentPos.pLike.toString()
        viewHolder.likeBtn.isSelected = currentPos.Vlike

        viewHolder.likeBtn.setOnClickListener {
            viewHolder.likeBtn.setImageResource(R.drawable.ic_like)
            currentPos.pLike++
            viewHolder.likeScore.text = currentPos.pLike.toString()
        }
    }



    override fun getItemCount(): Int {
        return postModelList.size
    }

}