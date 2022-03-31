package com.asilmedia.idmasters.fragments.fourmain

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.activities.RegisterAndLoginActivity
import com.asilmedia.idmasters.models.User
import com.asilmedia.idmasters.preference.MyPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var preference: MyPreference
    private var user = User()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        initializeVariables()
        getUser()
        binding.layoutAdmin.layoutAddNews.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_addNewsFragment)
        }
        binding.layoutAdmin.layoutCompetition.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_addCompetitionFragment)
        }
        binding.layoutAdmin.layoutSphere.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_addSphereFragment)
        }
        binding.layoutAdmin.layoutExit.setOnClickListener {
            preference.setString("reg", "no")
            val intent = Intent(requireContext(), RegisterAndLoginActivity::class.java)
            startActivity(intent)
            (activity as MainActivity).finish()
        }
        binding.layoutAdmin.layoutLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_changeLanguageFragment)
        }

        binding.layoutUser.layoutLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_changeLanguageFragment)
        }

        binding.layoutUser.layoutExit.setOnClickListener {
            preference.setString("reg", "no")
            val intent = Intent(requireContext(), RegisterAndLoginActivity::class.java)
            startActivity(intent)
            (activity as MainActivity).finish()
        }

        binding.layoutAdmin.layoutAddVideo.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_addVideoFragment)
        }
        binding.layoutAdmin.layoutAbout.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_showAboutAppFragment)
        }
        binding.layoutUser.layoutAbout.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_showAboutAppFragment)
        }

        binding.layoutAdmin.layoutConditions.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_conditionsFragment)
        }
        binding.layoutUser.layoutConditions.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_conditionsFragment)
        }

        binding.layoutAdmin.layoutPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_privacyFragment2)
        }
        binding.layoutUser.layoutPrivacy.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_privacyFragment2)
        }


        return binding.root
    }

    private fun initializeVariables() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        preference = MyPreference.getInstance(requireContext())
    }

    private fun getUser() {
        firestore.collection("users").document(auth.uid.toString()).get().addOnSuccessListener {
            user = it.toObject(User::class.java)!!
            if (user.role == "admin") {
                binding.layoutUser.layout.visibility = View.GONE
                binding.layoutAdmin.layout.visibility = View.VISIBLE
            } else
                if (user.role == "user") {
                    binding.layoutUser.layout.visibility = View.VISIBLE
                    binding.layoutAdmin.layout.visibility = View.GONE
                }
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.cannot_connect_to_server),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

}