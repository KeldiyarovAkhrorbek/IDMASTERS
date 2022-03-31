package com.asilmedia.idmasters.fragments.main.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.models.News
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentShowNewsBinding

class ShowNewsFragment : Fragment() {

    private lateinit var binding: FragmentShowNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowNewsBinding.inflate(inflater, container, false)
        val bundle = arguments
        val news = bundle?.getSerializable("news") as News
        Glide.with(requireContext()).load(news.imgUrl).placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder).into(binding.newsImage)
        binding.tvTitle.text = news.title
        binding.tvBody.text = news.body
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
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