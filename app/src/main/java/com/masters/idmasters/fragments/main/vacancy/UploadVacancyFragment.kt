package com.masters.idmasters.fragments.main.vacancy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.masters.idmasters.R
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.databinding.FragmentUploadVacancyBinding
import com.masters.idmasters.models.Sphere
import com.masters.idmasters.models.Vacancy


class UploadVacancyFragment : Fragment() {
    private lateinit var binding: FragmentUploadVacancyBinding
    private var imageUrl = ""
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var reference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private var spheresListString = ArrayList<String>()
    private lateinit var spheresSpinnerAdapter: ArrayAdapter<String>
    private var vacancy = Vacancy()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadVacancyBinding.inflate(inflater, container, false)
        initializeVariables()
        fetchSpheres()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivUploadPicture.setOnClickListener {
            onResultGallery()
        }
        binding.mcvUpload.setOnClickListener {
            if (isValid()) {
                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.check))
                builder.setMessage(getString(R.string.are_you_sure_all_the_information_is_correct))
                builder.setPositiveButton(getString(R.string.yes))
                { _, _ ->
                    firestore.collection("vacancies").document(vacancy.id.toString()).set(vacancy)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.successfully_uploaded),
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.problem_occured),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
                builder.show()
            }
        }




        return binding.root
    }


    private fun isValid(): Boolean {
        val company_name = binding.edCompanyName.text.toString().trim()
        val location = binding.edLocation.text.toString().trim()
        val requirements = binding.edRequirements.text.toString().trim()
        val offers = binding.edOffers.text.toString().trim()
        val rules = binding.edRules.text.toString().trim()
        val contact_number = binding.edContactNumber.text.toString().trim()
        val company_email = binding.edCompanyEmail.text.toString().trim()
        val salary = binding.edCompanyEmail.text.toString().trim()
        val telegram_username = binding.edTelegram.text.toString().trim()
        val speciality = spheresListString[binding.spinnerSpeciality.selectedItemPosition]
        val experience =
            binding.radio1.findViewById<RadioButton>(binding.radio1.checkedRadioButtonId).text.toString()
        val work_type =
            binding.radio2.findViewById<RadioButton>(binding.radio2.checkedRadioButtonId).text.toString()
        if (imageUrl == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.ivUploadPicture)
            return false
        } else if (company_name == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardCompanyName)
            binding.edCompanyName.requestFocus()
            return false
        } else if (location == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardLocation)
            binding.edLocation.requestFocus()
            return false
        } else if (requirements == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardRequirements)
            binding.edRequirements.requestFocus()
            return false
        } else if (offers == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardOffers)
            binding.edOffers.requestFocus()
            return false
        } else if (rules == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardRules)
            binding.edRules.requestFocus()
            return false
        } else if (contact_number == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardContactNumber)
            binding.edContactNumber.requestFocus()
            return false
        } else if (company_email == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardCompanyEmail)
            binding.edCompanyEmail.requestFocus()
            return false
        } else if (salary == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardSalary)
            binding.edSalary.requestFocus()
            return false
        } else if (telegram_username == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardTelegram)
            binding.edTelegram.requestFocus()
            return false
        } else if (binding.spinnerSpeciality.selectedItemPosition == 0) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardSpeciality)
            return false
        } else {
            vacancy.id = "${System.currentTimeMillis()}"
            vacancy.company_name = company_name
            vacancy.location = location
            vacancy.requirements = requirements
            vacancy.offers = offers
            vacancy.rules = rules
            vacancy.contact_number = contact_number
            vacancy.company_email = company_email
            vacancy.salary = salary
            vacancy.speciality = speciality
            vacancy.required_experience = experience
            vacancy.work_type = work_type
            vacancy.for_students = binding.checkbox.isChecked
            vacancy.imgUrl = imageUrl
            vacancy.telegram_username = telegram_username
            return true
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
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(binding.ivUploadPicture)
                                binding.lottie.visibility = View.GONE
                                binding.scrollView.alpha = 1F
                            }
                        }
                    }.addOnProgressListener { result ->
                        binding.lottie.visibility = View.VISIBLE
                        binding.scrollView.alpha = 0.1F
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.problem_while_loading_picture),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.lottie.visibility = View.GONE
                        binding.scrollView.alpha = 1F
                    }
            }
        }

    private fun fetchSpheres() {
        spheresListString = ArrayList()
        spheresListString.add(getString(R.string.technology))
        binding.lottie.visibility = View.VISIBLE
        binding.scrollView.isClickable = false
        binding.scrollView.alpha = 0.1F
        firestore.collection("spheres").get().addOnSuccessListener {
            binding.lottie.visibility = View.GONE
            binding.scrollView.alpha = 1F
            binding.scrollView.isClickable = true
            it.forEach { fff ->
                var sphere = fff.toObject(Sphere::class.java)
                spheresListString.add(sphere.title.toString())
            }
            spheresSpinnerAdapter =
                ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    spheresListString
                )
            binding.spinnerSpeciality.adapter = spheresSpinnerAdapter
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


    private fun initializeVariables() {
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        reference = firebaseStorage.getReference("${System.currentTimeMillis()}")
    }

}