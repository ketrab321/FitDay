package com.example.fitday.retrofit

data class Quotes (
    var quote : String,
    var length : String,
    var author : String,
    var tags : List<Any>,
    var category : String,
    var date : String,

    var title : String,
    var id : String){}
