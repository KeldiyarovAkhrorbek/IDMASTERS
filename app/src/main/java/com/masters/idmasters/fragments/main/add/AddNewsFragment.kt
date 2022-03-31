package com.masters.idmasters.fragments.main.add

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
import androidx.navigation.fragment.findNavController
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
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.databinding.FragmentAddNewsBinding
import com.masters.idmasters.models.News


class AddNewsFragment : Fragment() {

    private lateinit var binding: FragmentAddNewsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var imageUrl: String? = ""
    private lateinit var firestore: FirebaseFirestore
    private var news = News()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewsBinding.inflate(inflater, container, false)
        initializeVariables()
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            mcvPut.setOnClickListener {
                if (isValid()) {
                    val mills = System.currentTimeMillis()
                    news.id = "$mills"
                    firestore.collection("news").document("$mills").set(news).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            R.string.successfully_uploaded,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            R.string.problem_occured,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            mainImage.setOnClickListener {
                onResultGallery()
            }
        }
        return binding.root
    }

    private fun isValid(): Boolean {
        var array = resources.getStringArray(R.array.news_types)
        var title = binding.edTitle.text.toString().trim()
        var body = binding.edBody.text.toString().trim()
        if (title.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardTitle)
            binding.edTitle.requestFocus()
            return false
        } else if (body.isEmpty()) {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.cardBody)
            binding.edTitle.requestFocus()
            return false
        } else if (imageUrl == "") {
            YoYo.with(Techniques.Shake).duration(1000).playOn(binding.mainImage)
            return false
        } else {
            news.title = title
            news.body = body
            news.imgUrl = imageUrl
            news.type = array[binding.spinner.selectedItemPosition]
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
                storageReference.putFile(it)
                    .addOnSuccessListener { result ->
                        result.task.addOnSuccessListener {
                            val downloadUrl = result.metadata?.reference?.downloadUrl
                            downloadUrl?.addOnSuccessListener { uri ->
                                imageUrl = uri.toString()
                                Glide.with(this).load(imageUrl).centerCrop()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder).into(binding.mainImage)
                                binding.progress.visibility = View.GONE
                                binding.mainLayout.alpha = 1F
                                binding.mainLayout.isClickable = true
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.problem_while_loading_picture),
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnProgressListener {
                        binding.progress.visibility = View.VISIBLE
                        binding.mainLayout.alpha = 0.3F
                        binding.mainLayout.isClickable = false
                    }
            }

        }

    private fun initializeVariables() {
        auth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        val millis = System.currentTimeMillis()
        storageReference = firebaseStorage.getReference("$millis")
        firestore = FirebaseFirestore.getInstance()
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