package com.daily.nexamartpartner.features.auth.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Local fallback account store used only when the NexaMart auth backend is not configured. */
class LocalCredentialStore(context: Context) {
    private val preferences = EncryptedSharedPreferences.create(
        context.applicationContext,
        FILE_NAME,
        MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun createDeliveryAccount(name: String, email: String, password: CharArray): Result<Unit> {
        val normalizedEmail = email.trim().lowercase()
        if (preferences.getString(KEY_EMAIL, null) != null) {
            return Result.failure(IllegalStateException("A local delivery account already exists on this device."))
        }
        val salt = ByteArray(SALT_BYTES).also(SecureRandom()::nextBytes)
        val hash = hashPassword(password, salt)
        preferences.edit()
            .putString(KEY_NAME, name.trim())
            .putString(KEY_EMAIL, normalizedEmail)
            .putString(KEY_SALT, salt.toHex())
            .putString(KEY_HASH, hash.toHex())
            .apply()
        return Result.success(Unit)
    }

    fun authenticateDelivery(email: String, password: CharArray): DeliveryAccount? {
        val storedEmail = preferences.getString(KEY_EMAIL, null) ?: return null
        if (storedEmail != email.trim().lowercase()) return null
        val salt = preferences.getString(KEY_SALT, null)?.hexToBytes() ?: return null
        val expected = preferences.getString(KEY_HASH, null)?.hexToBytes() ?: return null
        val actual = hashPassword(password, salt)
        if (!MessageDigest.isEqual(expected, actual)) return null
        return DeliveryAccount(
            name = preferences.getString(KEY_NAME, "Delivery Partner").orEmpty(),
            email = storedEmail
        )
    }

    fun hasDeliveryAccount(): Boolean = preferences.getString(KEY_EMAIL, null) != null

    private fun hashPassword(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    data class DeliveryAccount(val name: String, val email: String)

    companion object {
        private const val FILE_NAME = "nexamart_local_credentials"
        private const val KEY_NAME = "delivery_name"
        private const val KEY_EMAIL = "delivery_email"
        private const val KEY_SALT = "delivery_salt"
        private const val KEY_HASH = "delivery_hash"
        private const val SALT_BYTES = 16
        private const val ITERATIONS = 120_000
        private const val KEY_BITS = 256

        private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
        private fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}
