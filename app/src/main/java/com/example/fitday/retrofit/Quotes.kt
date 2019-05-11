package com.example.fitday.retrofit

data class Quotes (
    var quote : String,
    var author : String,
    var length : String,
    var tags : List<Any>,
    var category : String,
    var title : String,
    var date : String,
    var id : Int){}
