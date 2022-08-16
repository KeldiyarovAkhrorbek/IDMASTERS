package com.asilmedia.idmasters.fragments.main.specialist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.adapters.specialist.viewpager.SpecialistViewpagerAdapter
import com.asilmedia.idmasters.adapters.specialist.viewpager.recycler.SpecialistsAdapter
import com.asilmedia.idmasters.adapters.specialist.viewpager.recycler.SphereAdapter
import com.asilmedia.idmasters.models.Resume
import com.asilmedia.idmasters.models.Sphere
import com.asilmedia.idmasters.models.SphereWithSelected
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentSpecialistBinding
import com.masters.idmasters.databinding.ItemTabBinding

class SpecialistFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sphereAdapter =
            SphereAdapter(requireContext(), object : SphereAdapter.SetOnItemClickListener {
                override fun setOnItemClick(sphereWithSelected: SphereWithSelected, position: Int) {
                    if (selectedSpherePos != position) {
                        listSphereWithSelected[selectedSpherePos].selected = false
                        listSphereWithSelected[position].selected = true
                        sphereAdapter.notifyItemChanged(selectedSpherePos)
                        sphereAdapter.notifyItemChanged(position)
                        selectedSpherePos = position
                        getCanditates(
                            listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                            selectedTabPos
                        )
                    }
                }
            })


        specialistsAdapter = SpecialistsAdapter(requireContext(),
            object : SpecialistsAdapter.SetOnItemClickListener {
                override fun setOnItemClick(resume: Resume) {
                    val bundle = Bundle()
                    bundle.putSerializable("specialist", resume)
                    findNavController().navigate(
                        R.id.action_specialistFragment_to_showSpecialistInfoFragment,
                        bundle
                    )
                }
            })
    }

    private lateinit var binding: FragmentSpecialistBinding
    private lateinit var firestore: FirebaseFirestore
    private var list = ArrayList<Sphere>()
    private var listCanditates = ArrayList<Resume>()
    private var listSphereWithSelected = ArrayList<SphereWithSelected>()
    private lateinit var sphereAdapter: SphereAdapter
    private lateinit var specialistViewpagerAdapter: SpecialistViewpagerAdapter
    private var selectedSpherePos = 0
    private var selectedTabPos = 0
    private lateinit var specialistsAdapter: SpecialistsAdapter
    private var isLoaded = false
    private val TAG = "SpecialistFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpecialistBinding.inflate(inflater, container, false)

        binding.recycler.adapter = sphereAdapter // initializing sphere

        binding.recyclerSpecialist.adapter = specialistsAdapter

        specialistViewpagerAdapter = SpecialistViewpagerAdapter(this)
        binding.viewpager.adapter = specialistViewpagerAdapter

        if (!isLoaded)
            makeView()

        binding.floatingButton.setOnClickListener {
            findNavController().navigate(R.id.action_specialistFragment_to_uploadResumeFragment)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.search.setOnClickListener {
            binding.search.isIconified = false
        }

        binding.search.queryHint = resources.getString(R.string.search)


        val list = ArrayList<String>()
        list.add(resources.getString(R.string.specialists))
        list.add(resources.getString(R.string.students))
//        list.add(resources.getString(R.string.upload_resume))

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager
        ) { tab: TabLayout.Tab, position: Int ->
            if (position < 2) {
                val itemTabBinding: ItemTabBinding = ItemTabBinding.inflate(layoutInflater)
                tab.customView = itemTabBinding.root
                itemTabBinding.text.text = list[position]
                if (selectedTabPos == position) {
                    binding.viewpager.currentItem = selectedTabPos
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
            }

        }.attach()


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                with(itemTabBinding) {
                    if (itemTabBinding.text.text.toString() == resources.getString(R.string.specialists)) {
                        selectedTabPos = 0
                        card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                        text.setTextColor(resources.getColor(R.color.white))
                    } else if (itemTabBinding.text.text.toString() == resources.getString(R.string.students)) {
                        selectedTabPos = 1
                        card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                        text.setTextColor(resources.getColor(R.color.white))
                    }
                }
                getCanditates(
                    listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                    selectedTabPos
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.white))
                    text.setTextColor(resources.getColor(R.color.tab_color))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                if (itemTabBinding.text.currentTextColor != resources.getColor(R.color.white)) {
                    with(itemTabBinding) {
                        if (itemTabBinding.text.text.toString() == resources.getString(R.string.specialists)) {
                            selectedTabPos = 0
                            card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                            text.setTextColor(resources.getColor(R.color.white))
                        } else if (itemTabBinding.text.text.toString() == resources.getString(R.string.students)) {
                            selectedTabPos = 1
                            card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                            text.setTextColor(resources.getColor(R.color.white))
                        }
                    }
                    getCanditates(
                        listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                        selectedTabPos
                    )
                }

            }
        })

        return binding.root
    }

    private fun makeView() {
        firestore = FirebaseFirestore.getInstance()
        selectedTabPos = 0
        selectedSpherePos = 0

        getSpheres()
    }

    private fun getSpheres() {
        binding.contentLayout.isClickable = false
        binding.contentLayout.alpha = 0.1f
        binding.progress.visibility = View.VISIBLE
        list = ArrayList()
        listSphereWithSelected = ArrayList()
        firestore.collection("spheres").get().addOnSuccessListener {
            it.forEach { queryDocumentSnapshot ->
                val sphere = queryDocumentSnapshot.toObject(Sphere::class.java)
                listSphereWithSelected.add(SphereWithSelected(sphere, false))
                list.add(sphere)
            }
            listSphereWithSelected[0].selected = true
            sphereAdapter.submitList(listSphereWithSelected)
            sphereAdapter.notifyDataSetChanged()
            binding.contentLayout.isClickable = true
            binding.contentLayout.alpha = 1f
            binding.progress.visibility = View.GONE
            getCanditates(
                listSphereWithSelected[selectedSpherePos].sphere?.title.toString(),
                selectedTabPos
            )


        }
    }

    private fun getCanditates(sphere: String, parameter: Int) {
        isLoaded = true
        binding.contentLayout.isClickable = false
        binding.contentLayout.alpha = 0.1f
        binding.progress.visibility = View.VISIBLE
        listCanditates = ArrayList()
        firestore.collection("resumes").get().addOnSuccessListener { result ->
            result.forEach {
                val resume = it.toObject(Resume::class.java)
                if (resume.speciality.toString().equals(sphere, true)) {
                    if (parameter == 0) {
                        if (resume.studies == false) {
                            listCanditates.add(resume)
                        }
                    } else if (parameter == 1) {
                        if (resume.studies == true) {
                            listCanditates.add(resume)
                        }
                    }
                }
            }
            binding.contentLayout.isClickable = true
            binding.contentLayout.alpha = 1f
            binding.progress.visibility = View.GONE
            specialistsAdapter.submitList(listCanditates)
            specialistsAdapter.notifyDataSetChanged()
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