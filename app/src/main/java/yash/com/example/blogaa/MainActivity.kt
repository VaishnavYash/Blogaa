package yash.com.example.blogaa

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import yash.com.example.blogaa.adapter.PostAdapter


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    var recyclerView: RecyclerView? =null
    var postAdapter: PostAdapter? = null
    val postModelList = arrayListOf<PostModel>()

    var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.blog_list)

        var layoutManager= LinearLayoutManager(this@MainActivity)
        layoutManager.stackFromEnd
        layoutManager.reverseLayout         //to Show latest post first

        recyclerView?.layoutManager= layoutManager


        var intent:Intent? = intent
        userName= intent?.getStringExtra("userName")
        loadPosts()

    }

    private fun loadPosts() {

        var ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postModelList.clear()

                for(ds in snapshot.children){
                    var postModel: PostModel = ds.getValue(PostModel::class.java)!!
                    postModelList.add(postModel)
                    postAdapter= PostAdapter(this@MainActivity, postModelList)
                    recyclerView?.adapter = postAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, ""+error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.action_add){
            var intent = Intent(this, postActivity::class.java)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }else if(item.itemId==R.id.logOut){
            mAuth?.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

