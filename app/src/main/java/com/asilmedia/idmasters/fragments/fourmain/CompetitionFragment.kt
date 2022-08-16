package com.asilmedia.idmasters.fragments.fourmain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.adapters.CompetitionAdapter
import com.asilmedia.idmasters.models.Competition
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentCompetitionBinding

class CompetitionFragment : Fragment() {
    private lateinit var binding: FragmentCompetitionBinding
    private lateinit var competitionAdapter: CompetitionAdapter
    private lateinit var firestore: FirebaseFirestore
    private var listCompetiton = ArrayList<Competition>()
    private var isLoaded = false
    private val TAG = "CompetitionFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        competitionAdapter = CompetitionAdapter(object : CompetitionAdapter.SetOnItemClickListener {
            override fun setOnItemClick(competition: Competition) {
                val bundle = Bundle()
                bundle.putSerializable("competition", competition)
                findNavController().navigate(
                    R.id.action_nav_star_to_showCompetitionFragment,
                    bundle
                )
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompetitionBinding.inflate(inflater, container, false)
        if (!isLoaded) {
            getList()
        }
        binding.rv.adapter = competitionAdapter
        return binding.root
    }

    private fun getList() {
        isLoaded = true
        listCompetiton = ArrayList()
        firestore.collection("competitions").get().addOnSuccessListener { result ->
            for (queryDocumentSnapshot in result) {
                val competition = queryDocumentSnapshot.toObject(Competition::class.java)
                listCompetiton.add(competition)
            }
            competitionAdapter.submitList(listCompetiton)
            competitionAdapter.notifyDataSetChanged()
        }
    }

}