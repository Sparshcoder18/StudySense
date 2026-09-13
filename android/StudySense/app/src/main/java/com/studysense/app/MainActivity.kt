package com.studysense.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.studysense.app.databinding.ActivityMainBinding

/**
 * Single-Activity host. All screens are Fragments driven by the nav graph
 * (nav_graph.xml). Keep this class thin - it should never contain business
 * logic or database calls (see code-quality rule: no DB calls in
 * Activities/Fragments).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // NavHostFragment is declared in activity_main.xml; no further setup
        // needed until BottomNavigationView is introduced in a later milestone.
    }
}
