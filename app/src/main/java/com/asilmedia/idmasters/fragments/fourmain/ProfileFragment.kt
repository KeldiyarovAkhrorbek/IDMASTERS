package com.asilmedia.idmasters.fragments.fourmain

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.asilmedia.idmasters.models.User
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.masters.idmasters.R
import com.masters.idmasters.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private val TAG = "ProfileFragment"
    private lateinit var binding: FragmentProfileBinding
    private var lastImageUrl: String = ""
    private var imageUrl = ""
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var reference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var user = User()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initializeVariables()
        binding.civProfile.setOnClickListener {
            onResultGallery()
        }
        binding.mcvOk.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.check))
            builder.setMessage(getString(R.string.are_you_sure_all_the_information_is_correct))
            builder.setPositiveButton(getString(R.string.yes))
            { _, _ ->
                updateUser()
            }
            builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
            builder.show()
        }
        return binding.root
    }

    private fun initializeVariables() {
        val millis = System.currentTimeMillis()
        firebaseStorage = FirebaseStorage.getInstance()
        reference = firebaseStorage.getReference("$millis")
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        getUser()
    }

    private fun updateUser() {
        if (lastImageUrl != "") {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(lastImageUrl)
            storageReference.delete()
        }
        if (isValid()) {
            firestore.collection("users").document(auth.uid.toString()).set(user)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        R.string.successfully_uploaded,
                        Toast.LENGTH_SHORT
                    ).show()
                    initializeVariables()
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        R.string.problem_occured,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }

    private fun isValid(): Boolean {
        val full_name: String = binding.edFullname.text.toString().trim()
        val phone_number: String = binding.edPhoneNumber.text.toString().trim()
        val country: String = binding.edCountry.text.toString().trim()
        if (full_name.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardFullname)
            binding.edFullname.requestFocus()
            return false
        } else if (country.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardCountry)
            binding.edCountry.requestFocus()
            return false
        } else if (phone_number.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardPhoneNumber)
            binding.edPhoneNumber.requestFocus()
            return false
        } else {
            user.full_name = full_name
            user.country = country
            user.phone_number = phone_number
            user.user_image = imageUrl
            return true
        }
    }

    private fun getUser() {
        firestore.collection("users").document(auth.uid.toString()).get().addOnSuccessListener {
            user = it.toObject(User::class.java)!!
            Glide.with(this).load(user.user_image).error(R.drawable.person_placeholer)
                .placeholder(R.drawable.person_placeholer).into(binding.civProfile)
            binding.apply {
                tvFullName.text = user.full_name
                tvEmail.text = user.email
                tvCountry.text = user.country
                edFullname.setText(user.full_name)
                edPhoneNumber.setText(user.phone_number)
                edCountry.setText(user.country)
            }
            if (user.user_image != null)
                lastImageUrl = user.user_image!!
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.cannot_connect_to_server),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun onResultGallery() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getImageContent.launch("image/*")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri =
                            Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {
                        response.requestedPermission
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.storage_permission))
                    builder.setMessage(getString(R.string.in_order_to_upload_you_should_gallery))
                    builder.setPositiveButton(getString(R.string.allow))
                    { _, _ ->
                        p1?.continuePermissionRequest()
                    }
                    builder.setNegativeButton(getString(R.string.dont_ask_again)) { _, _ -> p1?.cancelPermissionRequest() }
                    builder.show()

                }

            }).check()
    }

    private var getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                reference.putFile(it)
                    .addOnSuccessListener { result ->
                        result.task.addOnSuccessListener {
                            val downloadUrl = result.metadata?.reference?.downloadUrl
                            downloadUrl?.addOnSuccessListener { uri ->
                                imageUrl = uri.toString()
                                Glide.with(this).load(imageUrl).centerCrop()
                                    .placeholder(R.drawable.person_placeholer)
                                    .error(R.drawable.person_placeholer).into(binding.civProfile)
                                binding.progress.visibility = View.GONE
                                binding.nested.alpha = 1F
                            }
                        }
                    }.addOnProgressListener { result ->
                        binding.progress.visibility = View.VISIBLE
                        binding.nested.alpha = 0.3F
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.problem_while_loading_picture),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.GONE
                        binding.nested.alpha = 1F
                    }
            }
        }

}