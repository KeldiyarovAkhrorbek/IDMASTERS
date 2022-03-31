package com.masters.idmasters.fragments.main.changelanguage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.masters.idmasters.R
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.databinding.FragmentChangeLanguageBinding
import com.masters.idmasters.preference.MyPreference
import com.yariksoffice.lingver.Lingver

class ChangeLanguageFragment : Fragment() {
    private lateinit var binding: FragmentChangeLanguageBinding
    private lateinit var preference: MyPreference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeLanguageBinding.inflate(inflater, container, false)
        preference = MyPreference.getInstance(requireContext())
        val language = preference.getString("lang") ?: "en"

        Lingver.getInstance().setLocale(
            requireContext(),
            language
        )

        binding.apply {
            fun select(card: MaterialCardView, text: TextView, image: ImageView) {
                image.visibility = View.VISIBLE
            }

            when (language) {
                "ru" -> {
                    select(russian, text2, image2)
                }
                "uz" -> {
                    select(uzbek, text3, image3)
                }
                else -> {
                    select(english, text1, image1)
                }
            }

            fun clear() {
                image1.visibility = View.GONE
                image2.visibility = View.GONE
                image3.visibility = View.GONE
            }


            english.setOnClickListener {
                clear()
                select(english, text1, image1)
                preference.setString("lang", "en")
                Lingver.getInstance().setLocale(
                    requireContext(),
                    preference.getString("lang").toString()
                )

                binding.language.text = getString(R.string.language)
            }
            russian.setOnClickListener {
                clear()
                select(russian, text2, image2)
                preference.setString("lang", "ru")
                Lingver.getInstance().setLocale(
                    requireContext(),
                    preference.getString("lang").toString()
                )
                binding.language.text = getString(R.string.language)
            }
            uzbek.setOnClickListener {
                clear()
                select(uzbek, text3, image3)
                preference.setString("lang", "uz")
                Lingver.getInstance().setLocale(
                    requireContext(),
                    preference.getString("lang").toString()
                )
                binding.language.text = getString(R.string.language)
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

        }

        return binding.root
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