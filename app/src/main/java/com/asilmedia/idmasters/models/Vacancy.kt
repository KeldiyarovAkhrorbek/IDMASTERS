package com.asilmedia.idmasters.models

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
    constructor(
        id: String?,
        company_name: String?,
        location: String?,
        requirements: String?,
        offers: String?,
        rules: String?,
        contact_number: String?,
        company_email: String?,
        salary: String?,
        speciality: String?,
        required_experience: String?,
        work_type: String?,
        for_students: Boolean?,
        telegram_username: String?,
        imgUrl: String?
    ) {
        this.id = id
        this.company_name = company_name
        this.location = location
        this.requirements = requirements
        this.offers = offers
        this.rules = rules
        this.contact_number = contact_number
        this.company_email = company_email
        this.salary = salary
        this.speciality = speciality
        this.required_experience = required_experience
        this.work_type = work_type
        this.for_students = for_students
        this.telegram_username = telegram_username
        this.imgUrl = imgUrl
    }


}