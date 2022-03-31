package com.masters.idmasters.models

import java.io.Serializable

class News : Serializable {
    var id: String? = null
    var title: String? = null
    var body: String? = null
    var imgUrl: String? = null
    var type: String? = null

    constructor()

    constructor(id: String?, title: String?, body: String?, imgUrl: String?, type: String?) {
        this.id = id
        this.title = title
        this.body = body
        this.imgUrl = imgUrl
        this.type = type
    }
}
