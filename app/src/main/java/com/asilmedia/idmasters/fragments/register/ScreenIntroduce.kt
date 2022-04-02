package com.asilmedia.idmasters.fragments.register

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asilmedia.idmasters.activities.MainActivity
import com.asilmedia.idmasters.models.User
import com.asilmedia.idmasters.preference.MyPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.masters.idmasters.R
import com.masters.idmasters.databinding.LayoutIntroduceBinding


class ScreenIntroduce : Fragment() {
    private val client_id: String =
        "517414857128-kb9hbatg4s8gi8lgb7vd1r9sjtcr5oms.apps.googleusercontent.com"
    private lateinit var binding: LayoutIntroduceBinding
    private lateinit var myPreference: MyPreference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "ScreenIntroduce"
    private var RC_SIGN_IN = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutIntroduceBinding.inflate(inflater, container, false)
        myPreference = MyPreference.getInstance(requireContext())
        firebaseFirestore = FirebaseFirestore.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(client_id)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        auth = FirebaseAuth.getInstance()
        googleSignInClient.signOut()
        if (myPreference.getString("reg").toString() == "" || myPreference.getString("reg")
                .toString() == "no"
        ) {
            binding.mcvGoogle.setOnClickListener {
                signIn()
            }
        } else {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
        resultLauncher.launch(signInIntent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign in failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    var found = false
                    deployLoading()
                    firebaseFirestore.collection("users").get().addOnSuccessListener { result ->
                        for (queryDocumentSnapshot in result) {
                            val dbUser = queryDocumentSnapshot.toObject(User::class.java)
                            if (dbUser.uid == user?.uid) {
                                found = true
                                break
                            }
                        }
                        if (found) {
                            undoLoading()
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            undoLoading()
                            findNavController().navigate(R.id.action_screenIntroduce_to_screenRegistration)
                        }
                    }
                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun deployLoading() {
        binding.layout2.alpha = 0.5f
        binding.loadingAnim.visibility = View.VISIBLE
    }

    fun undoLoading() {
        binding.layout2.alpha = 1f
        binding.loadingAnim.visibility = View.GONE
    }

}