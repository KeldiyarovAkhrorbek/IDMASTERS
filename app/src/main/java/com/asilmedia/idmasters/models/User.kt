package com.asilmedia.idmasters.models

class User : java.io.Serializable {
    var uid: String? = null
    var full_name: String? = null
    var email: String? = null
    var phone_number: String? = null
    var user_image: String? = null
    var role: String? = "user"
    var country: String? = null

    constructor()
    constructor(
        uid: String?,
        full_name: String?,
        email: String?,
        phone_number: String?,
        user_image: String?,
        role: String?,
        country: String?
    ) {
        this.uid = uid
        this.full_name = full_name
        this.email = email
        this.phone_number = phone_number
        this.user_image = user_image
        this.role = role
        this.country = country
    }


}