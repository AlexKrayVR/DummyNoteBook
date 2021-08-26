package alex.com.notebook.ui

import alex.com.notebook.R
import alex.com.notebook.databinding.ActivityHostBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {

    lateinit var binding: ActivityHostBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //https://www.figma.com/file/SyBjne9SzxOdvkHMl6PdXGgA/Notebook-App-Design?node-id=0%3A1


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        setupActionBarWithNavController(navController)

    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()
}