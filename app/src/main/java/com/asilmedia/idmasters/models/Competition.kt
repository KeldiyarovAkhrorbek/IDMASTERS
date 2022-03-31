package com.asilmedia.idmasters.models

class Competition : java.io.Serializable {
    var id: String? = null
    var title: String? = null
    var body: String? = null
    var imgUrl: String? = null

    constructor()
    constructor(id: String?, title: String?, body: String?, imgUrl: String?) {
        this.id = id
        this.title = title
        this.body = body
        this.imgUrl = imgUrl
    }
}