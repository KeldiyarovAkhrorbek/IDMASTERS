package com.masters.idmasters.fragments.main.video

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
import com.masters.idmasters.databinding.FragmentAddVideoBinding
import com.masters.idmasters.models.Video

class AddVideoFragment : Fragment() {

    private lateinit var binding: FragmentAddVideoBinding
    private var imgUrl = ""
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var reference: StorageReference
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddVideoBinding.inflate(inflater, container, false)
        initializeVariables()
        binding.mcvPut.setOnClickListener {
            val video =
                Video("${System.currentTimeMillis()}", binding.edTitle.text.toString(), imgUrl)
            firestore.collection("videos").document(video.id.toString()).set(video)
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
        binding.mainImage.setOnClickListener {
            onResultGallery()
        }
        return binding.root
    }

    private fun initializeVariables() {
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        reference = firebaseStorage.getReference("${System.currentTimeMillis()}")
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
                                imgUrl = uri.toString()
                                Glide.with(this).load(imgUrl).centerCrop()
                                    .placeholder(R.drawable.person_placeholer)
                                    .error(R.drawable.person_placeholer)
                                    .into(binding.mainImage)
                                binding.progress.visibility = View.GONE
                                binding.mainLayout.alpha = 1F
                            }
                        }
                    }.addOnProgressListener { result ->
                        binding.progress.visibility = View.VISIBLE
                        binding.mainLayout.alpha = 0.1F
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.problem_while_loading_picture),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.GONE
                        binding.mainLayout.alpha = 1F
                    }
            }
        }


}