package com.example.taapp.Admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taapp.LoginRegister.StartActivity
import com.example.taapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminDashboard : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var totalUsersTextView: TextView
    private lateinit var activeUsersTextView: TextView
    private lateinit var onlineUsersTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var refreshButton: Button
    private lateinit var welcomeTextView: TextView

    private var userList = ArrayList<User>()
    private var filteredUserList = ArrayList<User>()

    // Add request code for edit user activity
    companion object {
        const val EDIT_USER_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        initializeViews()
        setupRecyclerView()
        setupSearchFunctionality()
        setupButtons()
        loadDummyUserData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning from edit activity
        refreshUserList()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewUsers)
        progressBar = findViewById(R.id.progressBar)
        searchEditText = findViewById(R.id.searchEditText)
        totalUsersTextView = findViewById(R.id.totalUsersTextView)
        activeUsersTextView = findViewById(R.id.activeUsersTextView)
        onlineUsersTextView = findViewById(R.id.onlineUsersTextView)
        logoutButton = findViewById(R.id.logoutButton)
        refreshButton = findViewById(R.id.refreshButton)
        welcomeTextView = findViewById(R.id.welcomeTextView)

        // Set welcome message
        welcomeTextView.text = "Welcome, Admin"
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(filteredUserList) { user ->
            showUserDetailsDialog(user)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
    }

    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterUsers(s.toString())
            }
        })
    }

    private fun setupButtons() {
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        refreshButton.setOnClickListener {
            loadDummyUserData()
        }
    }

    private fun refreshUserList() {
        // Sort users: online users first, then by name
        userList.sortWith(compareBy<User> { !it.isOnline }.thenBy { it.name.lowercase() })

        // Update filtered list based on current search
        val currentSearch = searchEditText.text.toString()
        filterUsers(currentSearch)

        // Update statistics
        val activeCount = userList.count { it.isActive }
        val onlineCount = userList.count { it.isOnline }
        updateStatistics(userList.size, activeCount, onlineCount)
    }

    private fun filterUsers(query: String) {
        filteredUserList.clear()
        if (query.isEmpty()) {
            filteredUserList.addAll(userList)
        } else {
            val queryLower = query.lowercase()
            for (user in userList) {
                if (user.name.lowercase().contains(queryLower) ||
                    user.email.lowercase().contains(queryLower) ||
                    user.iotCode.lowercase().contains(queryLower) ||
                    user.phone.contains(queryLower) ||
                    user.role.lowercase().contains(queryLower)) {
                    filteredUserList.add(user)
                }
            }
        }
        userAdapter.notifyDataSetChanged()
    }

    private fun loadDummyUserData() {
        progressBar.visibility = View.VISIBLE

        // Simulate loading delay
        android.os.Handler().postDelayed({
            userList.clear()

            // Create dummy users
            userList.add(User(
                id = "1",
                name = "Ribhi Gusti Zio",
                email = "ribhi@gmail.com",
                phone = "081282416460",
                iotCode = "IoT654321",
                role = "user",
                lastLogin = "14-07-2025 09:30:00",
                isActive = true,
                isOnline = true,
                registrationDate = "01-01-2025 10:00:00",
                lastSeen = "14-07-2025 09:30:00"
            ))

            userList.add(User(
                id = "2",
                name = "Kivlan Hakeem Arrouf",
                email = "kivlan@gmail.com",
                phone = "085711280122",
                iotCode = "IoT1234456",
                role = "Admin",
                lastLogin = "13-07-2025 15:45:00",
                isActive = true,
                isOnline = false,
                registrationDate = "05-01-2025 14:30:00",
                lastSeen = "13-07-2025 18:20:00"
            ))

            userList.add(User(
                id = "3",
                name = "Mochamad Irgi",
                email = "irgi@gmail.com",
                phone = "089530303292",
                iotCode = "IoT421678",
                role = "user",
                lastLogin = "12-07-2025 11:15:00",
                isActive = false,
                isOnline = false,
                registrationDate = "10-01-2025 09:00:00",
                lastSeen = "12-07-2025 16:30:00"
            ))

            userList.add(User(
                id = "4",
                name = "Alice Brown",
                email = "alice.brown@example.com",
                phone = "081234567893",
                iotCode = "IOT004",
                role = "user",
                lastLogin = "14-07-2025 08:00:00",
                isActive = true,
                isOnline = true,
                registrationDate = "15-01-2025 16:45:00",
                lastSeen = "14-07-2025 08:00:00"
            ))

            userList.add(User(
                id = "5",
                name = "Charlie Wilson",
                email = "charlie.wilson@example.com",
                phone = "081234567894",
                iotCode = "IOT005",
                role = "user",
                lastLogin = "11-07-2025 14:20:00",
                isActive = true,
                isOnline = false,
                registrationDate = "20-01-2025 11:30:00",
                lastSeen = "11-07-2025 17:45:00"
            ))

            userList.add(User(
                id = "6",
                name = "Diana Davis",
                email = "diana.davis@example.com",
                phone = "081234567895",
                iotCode = "IOT006",
                role = "user",
                lastLogin = "14-07-2025 10:15:00",
                isActive = true,
                isOnline = true,
                registrationDate = "25-01-2025 13:00:00",
                lastSeen = "14-07-2025 10:15:00"
            ))

            userList.add(User(
                id = "7",
                name = "Edward Miller",
                email = "edward.miller@example.com",
                phone = "081234567896",
                iotCode = "IOT007",
                role = "user",
                lastLogin = "09-07-2025 16:30:00",
                isActive = false,
                isOnline = false,
                registrationDate = "30-01-2025 15:20:00",
                lastSeen = "09-07-2025 19:45:00"
            ))

            userList.add(User(
                id = "8",
                name = "Fiona Garcia",
                email = "fiona.garcia@example.com",
                phone = "081234567897",
                iotCode = "IOT008",
                role = "user",
                lastLogin = "13-07-2025 12:00:00",
                isActive = true,
                isOnline = false,
                registrationDate = "02-02-2025 10:30:00",
                lastSeen = "13-07-2025 20:15:00"
            ))

            // Sort users: online users first, then by name
            userList.sortWith(compareBy<User> { !it.isOnline }.thenBy { it.name.lowercase() })

            filteredUserList.clear()
            filteredUserList.addAll(userList)
            userAdapter.notifyDataSetChanged()

            val activeCount = userList.count { it.isActive }
            val onlineCount = userList.count { it.isOnline }
            updateStatistics(userList.size, activeCount, onlineCount)

            progressBar.visibility = View.GONE
        }, 1000) // 1 second delay to simulate loading
    }

    private fun updateStatistics(totalUsers: Int, activeUsers: Int, onlineUsers: Int) {
        totalUsersTextView.text = "Total Users: $totalUsers"
        activeUsersTextView.text = "Active Users: $activeUsers"
        onlineUsersTextView.text = "Online Users: $onlineUsers"
    }

    private fun showUserDetailsDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_details, null)

        val nameTextView = dialogView.findViewById<TextView>(R.id.userNameTextView)
        val emailTextView = dialogView.findViewById<TextView>(R.id.userEmailTextView)
        val phoneTextView = dialogView.findViewById<TextView>(R.id.userPhoneTextView)
        val iotCodeTextView = dialogView.findViewById<TextView>(R.id.userIotCodeTextView)
        val roleTextView = dialogView.findViewById<TextView>(R.id.userRoleTextView)
        val statusTextView = dialogView.findViewById<TextView>(R.id.userStatusTextView)
        val lastLoginTextView = dialogView.findViewById<TextView>(R.id.userLastLoginTextView)
        val registrationDateTextView = dialogView.findViewById<TextView>(R.id.userRegistrationDateTextView)
        val editButton = dialogView.findViewById<Button>(R.id.editUserButton)
        val deleteButton = dialogView.findViewById<Button>(R.id.deleteUserButton)
        val toggleStatusButton = dialogView.findViewById<Button>(R.id.toggleStatusButton)

        nameTextView.text = "Name: ${user.name}"
        emailTextView.text = "Email: ${user.email}"
        phoneTextView.text = "Phone: ${user.phone}"
        iotCodeTextView.text = "IoT Code: ${user.iotCode}"
        roleTextView.text = "Role: ${user.role.replaceFirstChar { it.uppercase() }}"
        statusTextView.text = "Status: ${if (user.isActive) "Active" else "Inactive"}"
        lastLoginTextView.text = "Last Login: ${user.lastLogin.ifEmpty { "Never" }}"
        registrationDateTextView.text = "Registered: ${user.registrationDate.ifEmpty { "Unknown" }}"

        // Update toggle button text based on current status
        toggleStatusButton.text = if (user.isActive) "Deactivate User" else "Activate User"

        val dialog = AlertDialog.Builder(this)
            .setTitle("User Details")
            .setView(dialogView)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()

        editButton.setOnClickListener {
            dialog.dismiss()
            openEditUserActivity(user)
        }

        deleteButton.setOnClickListener {
            dialog.dismiss()
            showDeleteUserDialog(user)
        }

        toggleStatusButton.setOnClickListener {
            toggleUserStatus(user)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openEditUserActivity(user: User) {
        val intent = Intent(this, AdminEditUser::class.java)
        intent.putExtra("userId", user.id)
        intent.putExtra("userName", user.name)
        intent.putExtra("userEmail", user.email)
        intent.putExtra("userPhone", user.phone)
        intent.putExtra("userIotCode", user.iotCode)
        intent.putExtra("userRole", user.role)
        intent.putExtra("userStatus", user.isActive)
        intent.putExtra("userLastLogin", user.lastLogin)
        intent.putExtra("userIsOnline", user.isOnline)
        intent.putExtra("userRegistrationDate", user.registrationDate)
        intent.putExtra("userLastSeen", user.lastSeen)
        startActivityForResult(intent, EDIT_USER_REQUEST_CODE)
    }

    // Handle result from edit user activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let { resultData ->
                val updatedUser = resultData.getSerializableExtra(AdminEditUser.EXTRA_UPDATED_USER) as? User
                updatedUser?.let { user ->
                    updateUserInList(user)
                }
            }
        }
    }

    private fun updateUserInList(updatedUser: User) {
        // Find and update user in main list
        val userIndex = userList.indexOfFirst { it.id == updatedUser.id }
        if (userIndex != -1) {
            userList[userIndex] = updatedUser

            // Add success animation/feedback
            android.os.Handler().postDelayed({
                Toast.makeText(this, "✓ User ${updatedUser.name} updated successfully", Toast.LENGTH_SHORT).show()
            }, 100)
        }

        // Find and update user in filtered list
        val filteredIndex = filteredUserList.indexOfFirst { it.id == updatedUser.id }
        if (filteredIndex != -1) {
            filteredUserList[filteredIndex] = updatedUser
        }

        // Refresh the entire user list to ensure proper sorting and display
        refreshUserList()
    }

    private fun showDeleteUserDialog(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete user ${user.name}?\n\nThis action cannot be undone and will:\n• Remove all user data\n• Disconnect associated IoT devices\n• Clear user session")
            .setPositiveButton("Delete") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUser(user: User) {
        progressBar.visibility = View.VISIBLE

        // Simulate delete operation
        android.os.Handler().postDelayed({
            userList.remove(user)
            filteredUserList.remove(user)
            userAdapter.notifyDataSetChanged()

            val activeCount = userList.count { it.isActive }
            val onlineCount = userList.count { it.isOnline }
            updateStatistics(userList.size, activeCount, onlineCount)

            progressBar.visibility = View.GONE
            Toast.makeText(this, "User ${user.name} deleted successfully", Toast.LENGTH_SHORT).show()
        }, 1000)
    }

    private fun toggleUserStatus(user: User) {
        val newStatus = !user.isActive

        // Find the user in the list and update its status
        val userIndex = userList.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            val updatedUser = userList[userIndex].copy(
                isActive = newStatus,
                isOnline = if (newStatus) user.isOnline else false, // If deactivating, set offline
                lastSeen = if (!newStatus) SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date()) else user.lastSeen
            )
            userList[userIndex] = updatedUser

            // Update filtered list as well
            val filteredIndex = filteredUserList.indexOfFirst { it.id == user.id }
            if (filteredIndex != -1) {
                filteredUserList[filteredIndex] = updatedUser
            }

            userAdapter.notifyDataSetChanged()

            val activeCount = userList.count { it.isActive }
            val onlineCount = userList.count { it.isOnline }
            updateStatistics(userList.size, activeCount, onlineCount)

            val statusText = if (newStatus) "activated" else "deactivated"
            Toast.makeText(this, "User ${user.name} $statusText", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout from Admin Dashboard?")
            .setPositiveButton("Yes") { _, _ ->
                logoutAdmin()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logoutAdmin() {
        progressBar.visibility = View.VISIBLE

        // Simulate logout delay
        android.os.Handler().postDelayed({
            redirectToLogin()
        }, 500)
    }

    private fun redirectToLogin() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, StartActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        // Simulate setting admin as offline
    }

    override fun onDestroy() {
        super.onDestroy()
        // Simulate cleanup
    }
}

// User data class - make it Serializable for Intent passing
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val iotCode: String = "",
    val role: String = "user",
    val lastLogin: String = "",
    val isActive: Boolean = true,
    val isOnline: Boolean = false,
    val registrationDate: String = "",
    val lastSeen: String = ""
) : java.io.Serializable