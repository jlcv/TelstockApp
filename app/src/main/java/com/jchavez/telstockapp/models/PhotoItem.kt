package com.jchavez.telstockapp.models

class PhotoItem {
    var id: Int = 0
    var albumId: Int = 0
    var title: String
    var url: String
    var thumbnailUrl: String

    constructor(id: Int, albumId: Int, title: String, url: String, thumbnailUrl: String) {
        this.id = id
        this.albumId = albumId
        this.title = title
        this.url = url
        this.thumbnailUrl = thumbnailUrl

    }
}
