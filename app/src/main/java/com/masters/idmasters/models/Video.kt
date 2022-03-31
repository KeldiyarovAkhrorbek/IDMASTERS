package com.masters.idmasters.models

import java.io.Serializable

class Video : Serializable {
    var id: String? = null
    var imgUrl: String? = null
    var url: String? = null


    constructor()
    constructor(id: String?, imgUrl: String?, url: String?) {
        this.id = id
        this.imgUrl = imgUrl
        this.url = url
    }


}