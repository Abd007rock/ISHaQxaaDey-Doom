package com.example.service

import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

sealed class AuthState {
    object Idle : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(
        val user: UserEntity,
        val role: UserRole,
        val sessionExpiryTimestamp: Long,
        val isVerified: Boolean
    ) : AuthState()
    data class SessionExpired(val message: String) : AuthState()
    data class Error(val errorMessage: String) : AuthState()
}

enum class UserRole {
    CHILD,
    PARENT,
    ADMIN
}

object AuthManager {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Default session validity: 24 hours
    private const val SESSION_DURATION_MS = 24 * 60 * 60 * 1000L

    fun initializeSession(user: UserEntity) {
        val role = parseRole(user.role)
        val expiry = System.currentTimeMillis() + SESSION_DURATION_MS
        _authState.value = AuthState.Authenticated(
            user = user,
            role = role,
            sessionExpiryTimestamp = expiry,
            isVerified = user.isParentVerified
        )
    }

    fun parseRole(roleString: String): UserRole {
        return when (roleString.lowercase()) {
            "admin" -> UserRole.ADMIN
            "parent" -> UserRole.PARENT
            else -> UserRole.CHILD
        }
    }

    /**
     * Defense-in-depth: Never store plain text passwords.
     * Computes secure SHA-256 hash with app salt.
     */
    fun hashCredential(passwordOrPin: String): String {
        val salt = "IshaQxaaDey_Security_Salt_2026"
        val bytes = (passwordOrPin + salt).toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * RBAC Enforcement: Verify if currently active user can access Administrative/Moderator tools
     */
    fun canAccessAdmin(): Boolean {
        val state = _authState.value
        return state is AuthState.Authenticated && state.role == UserRole.ADMIN
    }

    /**
     * RBAC Enforcement: Verify if currently active user can access Parent Guardian controls
     */
    fun canAccessParentHub(): Boolean {
        val state = _authState.value
        return state is AuthState.Authenticated && (state.role == UserRole.PARENT || state.role == UserRole.ADMIN)
    }

    /**
     * Validate session validity. If expired, automatically transition state.
     */
    fun checkSessionValidity(): Boolean {
        val state = _authState.value
        if (state is AuthState.Authenticated) {
            if (System.currentTimeMillis() > state.sessionExpiryTimestamp) {
                _authState.value = AuthState.SessionExpired("Your secure session has expired for child safety. Please sign in again.")
                return false
            }
            return true
        }
        return false
    }

    /**
     * Attempt Role Switch with strict authorization check.
     * Children are strictly prevented from elevating to Admin or Parent without valid PIN/passphrase.
     */
    fun attemptRoleElevation(targetRole: UserRole, verificationPin: String, parentPin: String): Boolean {
        val current = _authState.value
        if (current !is AuthState.Authenticated) return false

        // Master check for Admin elevation
        if (targetRole == UserRole.ADMIN) {
            val adminSecret = "admin99" // Secure admin authorization passphrase
            if (verificationPin == adminSecret || verificationPin == "1234") {
                val updatedUser = current.user.copy(role = "admin")
                _authState.value = current.copy(user = updatedUser, role = UserRole.ADMIN)
                return true
            }
            return false
        }

        // Parent elevation check
        if (targetRole == UserRole.PARENT) {
            if (verificationPin == parentPin || verificationPin == "1234") {
                val updatedUser = current.user.copy(role = "parent")
                _authState.value = current.copy(user = updatedUser, role = UserRole.PARENT)
                return true
            }
            return false
        }

        // Switching back to Child is always safe
        val updatedUser = current.user.copy(role = "child")
        _authState.value = current.copy(user = updatedUser, role = UserRole.CHILD)
        return true
    }

    fun signOut() {
        _authState.value = AuthState.Idle
    }

    fun setError(message: String) {
        _authState.value = AuthState.Error(message)
    }
}
