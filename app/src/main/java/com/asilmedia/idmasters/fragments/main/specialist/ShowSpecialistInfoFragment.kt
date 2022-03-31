package com.asilmedia.idmasters.fragments.main.specialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.models.Resume
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentShowSpecialistInfoBinding


class ShowSpecialistInfoFragment : Fragment() {
    private lateinit var binding: FragmentShowSpecialistInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowSpecialistInfoBinding.inflate(inflater, container, false)
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val bundle = arguments
        val resume = bundle?.getSerializable("specialist") as Resume
        Glide.with(requireContext()).load(resume.imgUrl).error(R.drawable.person_placeholer)
            .placeholder(R.drawable.person_placeholer).into(binding.resumeImage)
        binding.apply {
            tvResumeFio.text = resume.full_name
            tvJob.text = resume.speciality + " ${getString(R.string.programmer)}"
            tvBirthDate.text = resume.birth_date
            tvLocation.text = resume.region
            tvPhoneNumber.text = resume.phone_number
            tvTelegram.text = resume.telegram_username
            tvEmail.text = resume.email
            tvEmail.text = resume.email
            if (resume.studies == false) {
                studyLayout.visibility = View.GONE
            } else {
                tvUniversity.text = resume.place_study
                tvMajor.text = resume.major
                tvCourse.text = resume.year
            }
            var skills = ""
            val split = resume.skills?.split(", ")
            split?.forEach {
                skills += it + "\n"
            }
            tvSkillsFull.text = skills
            tvExperience.text = resume.experience
            tvModeOfWork.text = resume.work_type
            tvEmail.isSelected = true
        }
        return binding.root
    }

    override fun onResume() {
        (activity as MainActivity).hideBottom()
        super.onResume()
    }

    override fun onPause() {
        (activity as MainActivity).showBottom()
        super.onPause()
    }
}