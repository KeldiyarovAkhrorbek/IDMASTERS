package com.masters.idmasters.fragments.main.vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.databinding.FragmentShowVacancyInfoBinding
import com.masters.idmasters.databinding.ItemOffersBinding
import com.masters.idmasters.databinding.ItemRequirementsBinding
import com.masters.idmasters.databinding.ItemRulesBinding
import com.masters.idmasters.models.Vacancy

class ShowVacancyInfoFragment : Fragment() {

    private lateinit var binding: FragmentShowVacancyInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowVacancyInfoBinding.inflate(inflater, container, false)
        val bundle = arguments
        val vacancy = bundle?.getSerializable("vacancy") as Vacancy
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        Glide.with(requireContext()).load(vacancy.imgUrl).placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder).into(binding.civCompany)
        binding.tvJob.text = vacancy.speciality + " " + getString(R.string.programmer)
        binding.tvCompanyName.text = vacancy.company_name
        binding.tvModeOfWorkFull.text = vacancy.work_type
        binding.tvSalaryFull.text = vacancy.salary
        binding.tvPhoneNumber.text = vacancy.contact_number
        binding.tvTelegram.text = vacancy.telegram_username
        binding.tvEmail.text = vacancy.company_email
        val requirements = vacancy.requirements?.split(", ")
        val rules = vacancy.rules?.split(", ")
        val offers = vacancy.offers?.split(", ")
        requirements?.forEach {
            addToRequirements(it)
        }

        rules?.forEach {
            addToRules(it)
        }

        offers?.forEach {
            addToOffers(it)
        }
        return binding.root
    }

    private fun addToRequirements(requirement: String) {
        val bindingRequirement = ItemRequirementsBinding.inflate(layoutInflater)
        bindingRequirement.tvRequirements.text = requirement
        binding.layoutRequirements.addView(bindingRequirement.root)
    }

    private fun addToRules(rule: String) {
        val bindingRule = ItemRulesBinding.inflate(layoutInflater)
        bindingRule.tvRules.text = rule
        binding.layoutRules.addView(bindingRule.root)
    }

    private fun addToOffers(offer: String) {
        val bindingRule = ItemOffersBinding.inflate(layoutInflater)
        bindingRule.tvOffers.text = offer
        binding.layoutOffers.addView(bindingRule.root)
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