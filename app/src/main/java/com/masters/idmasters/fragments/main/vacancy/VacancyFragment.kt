package com.masters.idmasters.fragments.main.vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.adapters.specialist.viewpager.SpecialistViewpagerAdapter
import com.masters.idmasters.adapters.specialist.viewpager.recycler.SphereAdapter
import com.masters.idmasters.adapters.vacancy.VacanciesAdapter
import com.masters.idmasters.databinding.FragmentVacancyBinding
import com.masters.idmasters.databinding.ItemTabBinding
import com.masters.idmasters.models.Sphere
import com.masters.idmasters.models.SphereWithSelected
import com.masters.idmasters.models.Vacancy

class VacancyFragment : Fragment() {
    private lateinit var binding: FragmentVacancyBinding
    private lateinit var firestore: FirebaseFirestore
    private var listSphere = ArrayList<Sphere>()
    private var listVacancy = ArrayList<Vacancy>()
    private var listSphereWithSelected = ArrayList<SphereWithSelected>()
    private lateinit var sphereAdapter: SphereAdapter
    private lateinit var specialistViewpagerAdapter: SpecialistViewpagerAdapter
    private var selectedSpherePos = 0
    private var selectedTabPos = 0
    private lateinit var vacanciesAdapter: VacanciesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVacancyBinding.inflate(inflater, container, false)
        makeView()
        return binding.root
    }

    private fun makeView() {
        selectedTabPos = 0
        selectedSpherePos = 0
        firestore = FirebaseFirestore.getInstance()
        sphereAdapter =
            SphereAdapter(requireContext(), object : SphereAdapter.SetOnItemClickListener {
                override fun setOnItemClick(sphereWithSelected: SphereWithSelected, position: Int) {
                    if (selectedSpherePos != position) {
                        listSphereWithSelected[selectedSpherePos].selected = false
                        listSphereWithSelected[position].selected = true
                        sphereAdapter.notifyItemChanged(selectedSpherePos)
                        sphereAdapter.notifyItemChanged(position)
                        selectedSpherePos = position
                        getVacancies(
                            listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                            selectedTabPos
                        )
                    }
                }
            })
        binding.recycler.adapter = sphereAdapter // initializing sphere
        getSpheres()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.search.setOnClickListener {
            binding.search.isIconified = false
        }
        vacanciesAdapter = VacanciesAdapter(requireContext(),
            object : VacanciesAdapter.SetOnItemClickListener {
                override fun setOnItemClick(vacancy: Vacancy) {
                    val bundle = Bundle()
                    bundle.putSerializable("vacancy", vacancy)
                    findNavController().navigate(
                        R.id.action_vacancyFragment_to_showVacancyInfoFragment,
                        bundle
                    )
                }
            })
        binding.recyclerSpecialist.adapter = vacanciesAdapter
        binding.search.queryHint = resources.getString(R.string.search)
        specialistViewpagerAdapter = SpecialistViewpagerAdapter(this)
        binding.viewpager.adapter = specialistViewpagerAdapter
        val list = ArrayList<String>()
        list.add(resources.getString(R.string.for_specialists))
        list.add(resources.getString(R.string.for_students))
        list.add(resources.getString(R.string.upload_vacancy))
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager
        ) { tab: TabLayout.Tab, position: Int ->
            val itemTabBinding: ItemTabBinding = ItemTabBinding.inflate(layoutInflater)
            tab.customView = itemTabBinding.root
            itemTabBinding.text.text = list[position]
            if (position == 0) {
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                    text.setTextColor(resources.getColor(R.color.white))
                }
            } else {
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.white))
                    text.setTextColor(resources.getColor(R.color.tab_color))
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                with(itemTabBinding) {
                    if (itemTabBinding.text.text.toString() == resources.getString(R.string.upload_vacancy)) {
                        findNavController().navigate(R.id.action_vacancyFragment_to_uploadVacancyFragment)
                    } else if (itemTabBinding.text.text.toString() == resources.getString(R.string.for_specialists)) {
                        selectedTabPos = 0
                        card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                        text.setTextColor(resources.getColor(R.color.white))
                    } else if (itemTabBinding.text.text.toString() == resources.getString(R.string.for_students)) {
                        selectedTabPos = 1
                        card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                        text.setTextColor(resources.getColor(R.color.white))
                    }
                    getVacancies(
                        listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                        selectedTabPos
                    )

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.white))
                    text.setTextColor(resources.getColor(R.color.tab_color))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun getSpheres() {
        binding.contentLayout.isClickable = false
        binding.contentLayout.alpha = 0.1f
        binding.progress.visibility = View.VISIBLE
        listSphere = ArrayList()
        listSphereWithSelected = ArrayList()
        firestore.collection("spheres").get().addOnSuccessListener {
            it.forEach { queryDocumentSnapshot ->
                val sphere = queryDocumentSnapshot.toObject(Sphere::class.java)
                listSphereWithSelected.add(SphereWithSelected(sphere, false))
                listSphere.add(sphere)
            }
            listSphereWithSelected[0].selected = true
            binding.contentLayout.isClickable = true
            binding.contentLayout.alpha = 1f
            binding.progress.visibility = View.GONE
            sphereAdapter.submitList(listSphereWithSelected)
            sphereAdapter.notifyDataSetChanged()
            getVacancies(
                listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                selectedTabPos
            )
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.problem_occured),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getVacancies(sphere: String, parameter: Int) {
        binding.contentLayout.isClickable = false
        binding.contentLayout.alpha = 0.1f
        binding.progress.visibility = View.VISIBLE
        listVacancy = ArrayList()
        firestore.collection("vacancies").get().addOnSuccessListener { result ->
            result.forEach {
                val vacancy = it.toObject(Vacancy::class.java)
                if (vacancy.speciality.toString().equals(sphere, true)) {
                    if (parameter == 0) {
                        if (vacancy.for_students == false) {
                            listVacancy.add(vacancy)
                        }
                    } else if (parameter == 1) {
                        if (vacancy.for_students == true) {
                            listVacancy.add(vacancy)
                        }
                    }
                }
            }
            binding.contentLayout.isClickable = true
            binding.contentLayout.alpha = 1f
            binding.progress.visibility = View.GONE
            vacanciesAdapter.submitList(listVacancy)
            vacanciesAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.problem_occured),
                Toast.LENGTH_SHORT
            ).show()
        }
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