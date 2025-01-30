package com.example.taapp.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.taapp.LoginRegister.StartActivity
import com.example.taapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Profile : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    // Override onCreateView untuk menginflate layout fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        val nameAccountTextView: TextView = view.findViewById(R.id.nameAccount)
        val emailTextView: TextView = view.findViewById(R.id.emailInput)
        val phoneTextView: TextView = view.findViewById(R.id.confPhone)
        val iotCodeTextView: TextView = view.findViewById(R.id.iotCodeInput)  // Tambahkan ID untuk IoT Code

        // Cek apakah data sudah ada di SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", android.content.Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        val email = sharedPreferences.getString("email", null)
        val phone = sharedPreferences.getString("phone", null)
        val iotCode = sharedPreferences.getString("iotCode", null)

        if (name != null && email != null && phone != null && iotCode != null) {
            // Menampilkan data yang disimpan di SharedPreferences
            nameAccountTextView.text = name
            emailTextView.text = email
            phoneTextView.text = phone
            iotCodeTextView.text = iotCode
        } else {
            // Jika tidak ada data di SharedPreferences, ambil data dari Firebase
            val user = auth.currentUser
            if (user != null) {
                // Ambil data pengguna dari Firebase Realtime Database
                val userId = user.uid
                database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val nameFromFirebase = snapshot.child("name").value.toString()
                        val emailFromFirebase = snapshot.child("email").value.toString()
                        val phoneFromFirebase = snapshot.child("phone").value.toString()
                        val iotCodeFromFirebase = snapshot.child("iotCode").value.toString()

                        // Menyimpan data ke SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("name", nameFromFirebase)
                        editor.putString("email", emailFromFirebase)
                        editor.putString("phone", phoneFromFirebase)
                        editor.putString("iotCode", iotCodeFromFirebase)
                        editor.apply()

                        // Menampilkan data di profile
                        nameAccountTextView.text = nameFromFirebase
                        emailTextView.text = emailFromFirebase
                        phoneTextView.text = phoneFromFirebase
                        iotCodeTextView.text = iotCodeFromFirebase
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Logout button
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_remember_logout, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = builder.create()

        val btnNegative = dialogView.findViewById<Button>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<Button>(R.id.btn_positive)

        // Negative button (No)
        btnNegative.setOnClickListener {
            alertDialog.dismiss() // Dismiss the dialog if user clicks No
        }

        // Positive button (Yes)
        btnPositive.setOnClickListener {
            logoutUser() // Proceed to logout if user clicks Yes
            alertDialog.dismiss()
        }

        // Show the dialog
        alertDialog.show()
    }

    private fun logoutUser() {
        // Clear saved account information from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all saved data
        editor.apply()

        // Logout from Firebase
        FirebaseAuth.getInstance().signOut()

        // Show a toast confirming the user has logged out
        Toast.makeText(requireContext(), "You have logged out successfully.", Toast.LENGTH_SHORT).show()

        // Navigate to StartActivity after logout
        val intent = Intent(activity, StartActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close the current activity so the user cannot go back
    }
}
