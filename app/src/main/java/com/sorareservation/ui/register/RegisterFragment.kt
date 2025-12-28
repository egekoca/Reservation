package com.sorareservation.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentRegisterBinding
import com.sorareservation.ui.login.LoginActivity

/**
 * Fragment for user registration
 */
class RegisterFragment : Fragment() {
    
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    companion object {
        /**
         * Create new instance of RegisterFragment
         */
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Restore saved state if available
        savedInstanceState?.let {
            binding.fullNameEditText.setText(it.getString("fullName", ""))
            binding.emailEditText.setText(it.getString("email", ""))
            binding.phoneEditText.setText(it.getString("phone", ""))
            binding.passwordEditText.setText(it.getString("password", ""))
        }
        
        binding.registerButton.setOnClickListener {
            attemptRegister()
        }
        
        binding.loginLinkTextView.setOnClickListener {
            navigateToLogin()
        }
    }
    
    private fun attemptRegister() {
        val fullName = binding.fullNameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        
        val user = SeferLab.register(email, password, fullName, phone)
        
        if (user != null) {
            Toast.makeText(requireContext(), R.string.registration_success, Toast.LENGTH_SHORT).show()
            // Navigate to login
            val intent = LoginActivity.newIntent(requireContext())
            startActivity(intent)
            requireActivity().finish()
        } else {
            Toast.makeText(requireContext(), "Email already exists", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToLogin() {
        val intent = LoginActivity.newIntent(requireContext())
        startActivity(intent)
        requireActivity().finish()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fullName", binding.fullNameEditText.text.toString())
        outState.putString("email", binding.emailEditText.text.toString())
        outState.putString("phone", binding.phoneEditText.text.toString())
        outState.putString("password", binding.passwordEditText.text.toString())
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

