package com.sorareservation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentLoginBinding
import com.sorareservation.ui.register.RegisterActivity
import com.sorareservation.ui.triplist.TripListActivity

/**
 * Fragment for user login
 */
class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    companion object {
        /**
         * Create new instance of LoginFragment
         */
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Restore saved state if available
        savedInstanceState?.let {
            binding.emailEditText.setText(it.getString("email", ""))
            binding.passwordEditText.setText(it.getString("password", ""))
        }
        
        binding.loginButton.setOnClickListener {
            attemptLogin()
        }
        
        binding.registerLinkTextView.setOnClickListener {
            navigateToRegister()
        }
    }
    
    private fun attemptLogin() {
        val context = context ?: return
        val activity = activity ?: return
        
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
            return
        }
        
        val user = SeferLab.login(email, password)
        
        if (user != null) {
            // Login successful, navigate to trip list
            val intent = TripListActivity.newIntent(context)
            startActivity(intent)
            activity.finish()
        } else {
            Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToRegister() {
        val context = context ?: return
        val intent = RegisterActivity.newIntent(context)
        startActivity(intent)
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.let {
            outState.putString("email", it.emailEditText.text.toString())
            outState.putString("password", it.passwordEditText.text.toString())
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

