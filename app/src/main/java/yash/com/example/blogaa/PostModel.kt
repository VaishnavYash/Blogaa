package yash.com.example.blogaa

data class PostModel(
    val pImage: String,
    val pTitle: String,
    val pDesc: String,
    val pName: String,
    var pLike : Int,
    var Vlike : Boolean
) {
    constructor() : this("", "", "", "",0,true )
}