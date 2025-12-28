package com.sorareservation.model

import java.util.UUID

/**
 * User model representing a user in the system
 */
data class User(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val isAdmin: Boolean = false
) {
    companion object {
        /**
         * Creates a user from email and password (for login)
         */
        fun create(email: String, password: String): User {
            return User(
                email = email,
                password = password,
                fullName = "",
                phone = ""
            )
        }
    }
}

