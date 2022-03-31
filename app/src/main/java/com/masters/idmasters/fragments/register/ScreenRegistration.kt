package com.masters.idmasters.fragments.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.databinding.LayoutRegistrationBinding
import com.masters.idmasters.models.User


class ScreenRegistration : Fragment() {
    private lateinit var binding: LayoutRegistrationBinding
    private var user: User = User()
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutRegistrationBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mcvRegister.setOnClickListener {
            removeErrors()
            if (isValid()) {
                firebaseFirestore.collection("users").document(user.uid.toString()).set(user)
                    .addOnCompleteListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.successfully_registered),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.problem_while_registration),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }
        }

        return binding.root
    }

    private fun isValid(): Boolean {
        var full_name: String = binding.edFullname.text.toString().trim()
        var phone_number: String = binding.edPhoneNumber.text.toString().trim()
        var country: String = binding.edCountry.text.toString().trim()
        if (full_name.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardFullname)
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardFullname)
            binding.edFullname.requestFocus()
            return false
        } else if (country.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardCountry)
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardCountry)
            binding.edCountry.requestFocus()
            return false
        } else if (phone_number.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardPhoneNumber)
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardPhoneNumber)
            binding.edPhoneNumber.requestFocus()
            return false
        } else {
            user.full_name = full_name
            user.uid = auth.uid
            user.role = "user"
            user.country = country
            user.email = auth.currentUser?.email
            user.phone_number = phone_number
            return true
        }
    }

    private fun removeErrors() {
        binding.layoutFullName.error = null
        binding.layoutFullName.isErrorEnabled = false
        binding.layoutCountry.error = null
        binding.layoutCountry.isErrorEnabled = false
        binding.layoutPhoneNumber.error = null
        binding.layoutPhoneNumber.isErrorEnabled = false
    }


}