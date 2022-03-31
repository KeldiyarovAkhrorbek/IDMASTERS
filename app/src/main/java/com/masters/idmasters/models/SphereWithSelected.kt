package com.masters.idmasters.models

import java.io.Serializable

class SphereWithSelected : Serializable {
    var sphere: Sphere? = null
    var selected: Boolean? = null

    constructor()
    constructor(sphere: Sphere?, selected: Boolean?) {
        this.sphere = sphere
        this.selected = selected
    }


}
