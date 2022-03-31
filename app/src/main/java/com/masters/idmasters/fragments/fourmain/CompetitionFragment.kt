package com.masters.idmasters.fragments.fourmain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.adapters.CompetitionAdapter
import com.masters.idmasters.databinding.FragmentCompetitionBinding
import com.masters.idmasters.models.Competition

class CompetitionFragment : Fragment() {
    private lateinit var binding: FragmentCompetitionBinding
    private lateinit var competitionAdapter: CompetitionAdapter
    private lateinit var firestore: FirebaseFirestore
    private var listCompetiton = ArrayList<Competition>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompetitionBinding.inflate(inflater, container, false)
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
        getList()
        binding.rv.adapter = competitionAdapter
        return binding.root
    }

    private fun getList() {
        listCompetiton = ArrayList()
        firestore.collection("competitions").get().addOnSuccessListener { result ->
            for (queryDocumentSnapshot in result) {
                val competition = queryDocumentSnapshot.toObject(Competition::class.java)
                listCompetiton.add(competition)
            }
//            var reversed = listCompetiton.reversed().toList()
            competitionAdapter.submitList(listCompetiton)
            competitionAdapter.notifyDataSetChanged()
        }
    }

}