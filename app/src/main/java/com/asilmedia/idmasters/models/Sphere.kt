package com.asilmedia.idmasters.models

import java.io.Serializable

class Sphere : Serializable {
    var title: String? = null
    var imgUrl: String? = null

    constructor()
    constructor(title: String?, imgUrl: String?) {
        this.title = title
        this.imgUrl = imgUrl
    }

    override fun toString(): String {
        return "Sphere(title=$title, imgUrl=$imgUrl)"
    }


}
