package com.masters.idmasters.models

import java.io.Serializable

class Resume : Serializable {
    var id: String? = null
    var full_name: String? = null
    var birth_date: String? = null
    var region: String? = null
    var phone_number: String? = null
    var telegram_username: String? = null
    var email: String? = null
    var link_resume: String? = null
    var skills: String? = null
    var speciality: String? = null
    var experience: String? = null
    var work_type: String? = null
    var studies: Boolean? = null
    var place_study: String? = null
    var major: String? = null
    var year: String? = null
    var imgUrl: String? = null

    constructor()
    constructor(
        id: String?,
        full_name: String?,
        birth_date: String?,
        region: String?,
        phone_number: String?,
        telegram_username: String?,
        email: String?,
        link_resume: String?,
        skills: String?,
        speciality: String?,
        experience: String?,
        work_type: String?,
        studies: Boolean?,
        place_study: String?,
        major: String?,
        year: String?
    ) {
        this.id = id
        this.full_name = full_name
        this.birth_date = birth_date
        this.region = region
        this.phone_number = phone_number
        this.telegram_username = telegram_username
        this.email = email
        this.link_resume = link_resume
        this.skills = skills
        this.speciality = speciality
        this.experience = experience
        this.work_type = work_type
        this.studies = studies
        this.place_study = place_study
        this.major = major
        this.year = year
    }


}