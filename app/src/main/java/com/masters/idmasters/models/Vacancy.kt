package com.masters.idmasters.models

import java.io.Serializable

class Vacancy : Serializable {
    var id: String? = null
    var company_name: String? = null
    var location: String? = null
    var requirements: String? = null
    var offers: String? = null
    var rules: String? = null
    var contact_number: String? = null
    var company_email: String? = null
    var salary: String? = null
    var speciality: String? = null
    var required_experience: String? = null
    var work_type: String? = null
    var for_students: Boolean? = null
    var telegram_username: String? = null
    var imgUrl: String? = null

    constructor()


}