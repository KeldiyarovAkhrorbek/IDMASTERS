package com.asilmedia.idmasters.fragments.viewpagernews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.adapters.NewsHorizontalAdapter
import com.asilmedia.idmasters.adapters.NewsVerticalAdapter
import com.asilmedia.idmasters.models.News
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentNewestBinding

class NewestFragment : Fragment() {
    private lateinit var binding: FragmentNewestBinding
    private lateinit var firestore: FirebaseFirestore
    private var listTop = ArrayList<News>()
    private var listBottom = ArrayList<News>()
    private lateinit var horizontalAdapter: NewsHorizontalAdapter
    private lateinit var verticalAdapter: NewsVerticalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        horizontalAdapter =
            NewsHorizontalAdapter(object : NewsHorizontalAdapter.SetOnItemClickListener {
                override fun setOnItemClick(news: News) {
                    val bundle = Bundle()
                    bundle.putSerializable("news", news)
                    findNavController().navigate(
                        R.id.action_newsFragment_to_showNewsFragment,
                        bundle
                    )
                }
            })

        verticalAdapter =
            NewsVerticalAdapter(object : NewsVerticalAdapter.SetOnItemClickListener {
                override fun setOnItemClick(news: News) {
                    val bundle = Bundle()
                    bundle.putSerializable("news", news)
                    findNavController().navigate(
                        R.id.action_newsFragment_to_showNewsFragment,
                        bundle
                    )
                }
            })
    }

    private var isLoaded = false
    private val TAG = "NewestFragment"
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewestBinding.inflate(inflater, container, false)
//        Log.d(TAG, "onCreateView: loaded: $isLoaded")
        binding.rvHorizontal.adapter = horizontalAdapter
        binding.rvVertical.adapter = verticalAdapter
        if (!isLoaded)
            getNews()


        return binding.root
    }

    private fun getNews() {
//        Log.d(TAG, "getNews: getting news")
        isLoaded = true
        listTop = ArrayList()
        listBottom = ArrayList()
        firestore.collection("news").get().addOnSuccessListener { result ->
            for (queryDocumentSnapshot in result) {
                val news = queryDocumentSnapshot.toObject(News::class.java)
                if (news.type.equals("TOP", true)) {
                    listTop.add(news)
                } else {
                    listBottom.add(news)
                }
            }
            horizontalAdapter.submitList(listTop.asReversed())
            verticalAdapter.submitList(listBottom.asReversed())
            horizontalAdapter.notifyDataSetChanged()
            verticalAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.problem_occured),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}