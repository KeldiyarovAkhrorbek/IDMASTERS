package com.masters.idmasters.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.masters.idmasters.R
import com.masters.idmasters.databinding.ActivityMainBinding
import com.masters.idmasters.preference.MyPreference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var preference: MyPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.setupWithNavController(navController)
        preference = MyPreference.getInstance(this)
        preference.setString("reg", "yes")
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()

    fun hideBottom() {
        YoYo.with(Techniques.SlideOutDown).duration(1000).playOn(binding.bottomNav)
        binding.bottomNav.visibility = View.GONE
    }

    fun showBottom() {
        YoYo.with(Techniques.SlideInUp).duration(1000).playOn(binding.bottomNav)
        binding.bottomNav.visibility = View.VISIBLE
    }

}