package com.asilmedia.idmasters.fragments.fourmain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.adapters.MenuAdapter
import com.asilmedia.idmasters.models.Menu
import com.asilmedia.idmasters.models.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var firestore: FirebaseFirestore
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        menuAdapter = MenuAdapter(object : MenuAdapter.SetOnItemClickListener {
            override fun setOnItemClick(menu: Menu) {
                if (menu.key == "news") {
                    findNavController().navigate(R.id.action_mainFragment_to_newsFragment)
                } else if (menu.key == "specialists") {
                    findNavController().navigate(R.id.action_mainFragment_to_specialistFragment)
                } else if (menu.key == "vacancies") {
                    findNavController().navigate(R.id.action_mainFragment_to_vacancyFragment)
                } else {
                    findNavController().navigate(R.id.action_nav_home_to_videoFragment2)
                }
            }
        })
    }

    private val TAG = "MainFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.rv.adapter = menuAdapter
        if (!isLoaded) {
            getUser()
            initialize()
        }


        return binding.root
    }

    private fun initialize() {
        var list = ArrayList<Menu>()
        list.add(
            Menu(
                getString(R.string.news_from_it_sphere),
                R.drawable.news,
                getString(R.string.news_from_it_sphere_full),
                "news"
            )
        )

        list.add(
            Menu(
                getString(R.string.highly_qualified_specialists),
                R.drawable.specialists,
                getString(R.string.find_professionals_for_team),
                "specialists"
            )
        )

        list.add(
            Menu(
                getString(R.string.best_vacansies),
                R.drawable.vacancies,
                getString(R.string.find_work_upload_resume),
                "vacancies"
            )
        )

        list.add(
            Menu(
                getString(R.string.programming_courses),
                R.drawable.courses,
                getString(R.string.learn_programming_through_courses),
                "courses"
            )
        )


        menuAdapter.submitList(list)
    }

    private fun getUser() {
        firestore.collection("users").document(auth.uid.toString()).get()
            .addOnSuccessListener { result ->
                val dbUser = result.toObject(User::class.java)
                Glide.with(requireContext()).load(dbUser?.user_image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder).into(binding.profileImage)

            }
    }
}