package com.asilmedia.idmasters.fragments.main.specialist

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.models.Resume
import com.asilmedia.idmasters.models.Sphere
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
import com.masters.idmasters.databinding.FragmentUploadResumeBinding
import java.util.*

class UploadResumeFragment : Fragment() {
    private lateinit var binding: FragmentUploadResumeBinding
    private var progress = 11
    private var imageUrl = ""
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var reference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private var spheresListString = ArrayList<String>()
    private lateinit var spheresSpinnerAdapter: ArrayAdapter<String>
    private var resume = Resume()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadResumeBinding.inflate(inflater, container, false)
        initializeVariables()
        binding.studyLayout.visibility = View.GONE
        fetchSpheres()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.checkbox.setOnCheckedChangeListener { p0, p1 ->
            if (!p1) {
                binding.studyLayout.visibility = View.GONE
            } else {
                binding.studyLayout.visibility = View.VISIBLE
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
        binding.ivUploadPicture.setOnClickListener {
            onResultGallery()
        }
        binding.edBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month1: Int = calendar.get(Calendar.MONTH)
            val year1: Int = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog,
                { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val date = "${formatDayMonth(dayOfMonth)}.${formatDayMonth(month + 1)}.${
                        formatDayMonth(
                            year
                        )
                    }"
                    binding.edBirthDate.setText(date)
                }, year1, month1, day
            )
            datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        } // birth date
        binding.mcvUpload.setOnClickListener {
            if (isValid()) {
                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.check))
                builder.setMessage(getString(R.string.are_you_sure_all_the_information_is_correct))
                builder.setPositiveButton(getString(R.string.yes))
                { _, _ ->
                    firestore.collection("resumes").document(resume.id.toString()).set(resume)
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

    private fun initializeVariables() {
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        reference = firebaseStorage.getReference("${System.currentTimeMillis()}")
    }

    private fun isValid(): Boolean {
        val full_name = binding.edFullname.text.toString().trim()
        val birth_date = binding.edBirthDate.text.toString().trim()
        val region = binding.edRegion.text.toString().trim()
        val phone_number = binding.edPhoneNumber.text.toString().trim()
        val tg_username = binding.edTelegramUsername.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val link_resume = binding.edLinkResume.text.toString().trim()
        val skills = binding.edSkills.text.toString().trim()
        val sphere = spheresListString[binding.spinnerSpeciality.selectedItemPosition]
        val studies = binding.checkbox.isChecked
        val experience =
            binding.radio1.findViewById<RadioButton>(binding.radio1.checkedRadioButtonId).text.toString()
        val work_type =
            binding.radio2.findViewById<RadioButton>(binding.radio2.checkedRadioButtonId).text.toString()
        if (imageUrl == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.ivUploadPicture)
            return false
        } else
            if (full_name.length < 5) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardFullname)
                binding.edFullname.requestFocus()
                return false
            } else if (birth_date.isEmpty()) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardBirthDate)
                return false
            } else if (region.length <= 3) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardRegion)
                binding.edRegion.requestFocus()
                return false
            } else if (phone_number.length < 7) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardPhoneNumber)
                binding.edPhoneNumber.requestFocus()
                return false
            } else if (tg_username.length < 2) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardTelegramUsername)
                binding.edTelegramUsername.requestFocus()
                return false
            } else if (email.length < 6) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardEmail)
                binding.edEmail.requestFocus()
                return false
            } else if (link_resume.length < 6) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardLinkResume)
                binding.edLinkResume.requestFocus()
                return false
            } else if (skills.length < 3) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardSkills)
                binding.edSkills.requestFocus()
                return false
            } else if (binding.spinnerSpeciality.selectedItemPosition == 0) {
                YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardSpeciality)
                return false
            } else if (!studies) {
                resume.id = System.currentTimeMillis().toString()
                resume.full_name = full_name
                resume.birth_date = birth_date
                resume.region = region
                resume.phone_number = phone_number
                resume.telegram_username = tg_username
                resume.email = email
                resume.link_resume = link_resume
                resume.skills = skills
                resume.speciality = sphere
                resume.studies = false
                resume.imgUrl = imageUrl
                resume.experience = experience
                resume.work_type = work_type
                return true
            } else {
                val study_place = binding.edPlaceOfStudy.text.toString().trim()
                val major = binding.edMajor.text.toString().trim()
                val year = binding.edYearOfStudy.text.toString().trim()
                if (study_place.length < 3) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardPlaceOfStudy)
                    binding.edPlaceOfStudy.requestFocus()
                    return false
                } else if (major.length < 3) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardMajor)
                    binding.edMajor.requestFocus()
                    return false
                } else if (year.isEmpty()) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardYearOfStudy)
                    binding.edYearOfStudy.requestFocus()
                    return false
                } else {
                    resume.id = System.currentTimeMillis().toString()
                    resume.full_name = full_name
                    resume.birth_date = birth_date
                    resume.region = region
                    resume.phone_number = phone_number
                    resume.telegram_username = tg_username
                    resume.email = email
                    resume.link_resume = link_resume
                    resume.skills = skills
                    resume.speciality = sphere
                    resume.studies = true
                    resume.place_study = study_place
                    resume.major = major
                    resume.year = year
                    resume.imgUrl = imageUrl
                    resume.experience = experience
                    resume.work_type = work_type
                    return true
                }
            }
    }

    private fun formatDayMonth(text: Int): String {
        if (text.toString().length == 1) {
            return "0$text"
        }
        return text.toString()
    }

    override fun onResume() {
        (activity as MainActivity).hideBottom()
        super.onResume()
    }

    override fun onPause() {
        (activity as MainActivity).showBottom()
        super.onPause()
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

}