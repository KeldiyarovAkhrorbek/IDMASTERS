package com.masters.idmasters.fragments.videos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.adapters.VideoAdapter
import com.masters.idmasters.databinding.FragmentVideoBinding
import com.masters.idmasters.models.Video

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var firestore: FirebaseFirestore
    private var list = ArrayList<Video>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        videoAdapter = VideoAdapter(object : VideoAdapter.SetOnItemClickListener {
            override fun setOnItemClick(video: Video) {
                gotoUrl(video.url.toString())
            }
        })
        binding.recycler.adapter = videoAdapter
        firestore = FirebaseFirestore.getInstance()
        getVideos()

        return binding.root
    }

    private fun getVideos() {
        list = ArrayList()
        firestore.collection("videos").get().addOnSuccessListener { result ->
            result.forEach {
                var video = it.toObject(Video::class.java)
                list.add(video)
            }
            videoAdapter.submitList(list)
            videoAdapter.notifyDataSetChanged()
        }
    }

    private fun gotoUrl(url: String) {
        val parse = Uri.parse(url)
        startActivity(Intent(Intent.ACTION_VIEW, parse))
    }


}