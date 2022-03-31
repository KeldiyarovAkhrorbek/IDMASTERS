package com.asilmedia.idmasters.fragments.main.competition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.models.Competition
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentShowCompetitionBinding

class ShowCompetitionFragment : Fragment() {
    private lateinit var binding: FragmentShowCompetitionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowCompetitionBinding.inflate(inflater, container, false)
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val bundle = arguments
        val competition = bundle?.getSerializable("competition") as Competition
        binding.apply {
            Glide.with(requireContext()).load(competition.imgUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(binding.newsImage)
            binding.tvTitle.text = competition.title
            binding.tvBody.text = competition.body
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