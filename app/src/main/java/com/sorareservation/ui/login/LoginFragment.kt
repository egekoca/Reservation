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
 * Kullanıcı giriş ekranı Fragment'ı
 * 
 * Bu Fragment, kullanıcının email ve şifre ile giriş yapmasını sağlar.
 * Giriş başarılı olursa TripListActivity'ye yönlendirilir.
 */
class LoginFragment : Fragment() {
    
    // ViewBinding için null-safe pattern
    // _binding private, binding public (sadece null değilken erişilebilir)
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    companion object {
        /**
         * Yeni LoginFragment instance'ı oluşturur
         * 
         * @return Yeni LoginFragment instance'ı
         */
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
    
    /**
     * Fragment'ın view'ını oluşturur
     * ViewBinding kullanarak layout'u inflate eder
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    /**
     * View oluşturulduktan sonra çağrılır
     * Burada UI elementlerine listener'lar eklenir ve state restore edilir
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Ekran döndürme durumunda kaydedilmiş state'i geri yükle
        // Kullanıcının yazdığı email ve şifre kaybolmasın
        savedInstanceState?.let {
            binding.emailEditText.setText(it.getString("email", ""))
            binding.passwordEditText.setText(it.getString("password", ""))
        }
        
        // Login butonuna tıklama listener'ı ekle
        binding.loginButton.setOnClickListener {
            attemptLogin()
        }
        
        // Register linkine tıklama listener'ı ekle
        // Kayıt ekranına yönlendirir
        binding.registerLinkTextView.setOnClickListener {
            navigateToRegister()
        }
    }
    
    /**
     * Kullanıcı girişi yapmayı dener
     * 
     * İşlem adımları:
     * 1. Email ve şifreyi al
     * 2. Boş kontrolü yap
     * 3. SeferLab.login() ile veritabanından kullanıcıyı kontrol et
     * 4. Başarılı ise TripListActivity'ye git, başarısız ise hata mesajı göster
     */
    private fun attemptLogin() {
        val context = context ?: return // Context null kontrolü
        val activity = activity ?: return // Activity null kontrolü
        
        // Input alanlarından email ve şifreyi al (trim ile baş/son boşlukları temizle)
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        
        // Boş alan kontrolü
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
            return
        }
        
        // SeferLab üzerinden giriş yap
        // Veritabanında email ve şifre eşleşen kullanıcıyı arar
        val user = SeferLab.login(email, password)
        
        if (user != null) {
            // Giriş başarılı
            // TripListActivity'ye yönlendir
            val intent = TripListActivity.newIntent(context)
            startActivity(intent)
            // Login ekranını kapat (geri tuşu ile geri dönülemesin)
            activity.finish()
        } else {
            // Giriş başarısız (email veya şifre yanlış)
            Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Kayıt ekranına yönlendirir
     * RegisterActivity'yi açar
     */
    private fun navigateToRegister() {
        val context = context ?: return
        val intent = RegisterActivity.newIntent(context)
        startActivity(intent)
    }
    
    /**
     * Ekran döndürme durumunda state'i kaydeder
     * 
     * Kullanıcının yazdığı email ve şifreyi Bundle'a kaydeder
     * Böylece ekran döndüğünde veriler kaybolmaz
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.let {
            outState.putString("email", it.emailEditText.text.toString())
            outState.putString("password", it.passwordEditText.text.toString())
        }
    }
    
    /**
     * View yok edildiğinde çağrılır
     * 
     * Memory leak önlemek için _binding'i null yap
     * ViewBinding, view'a referans tuttuğu için null yapılmazsa memory leak olabilir
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

