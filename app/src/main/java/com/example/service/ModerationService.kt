package com.example.service

import java.util.regex.Pattern

data class ModerationResult(
    val isSafe: Boolean,
    val violationReason: String? = null,
    val sanitizedText: String = "",
    val riskLevel: RiskLevel = RiskLevel.SAFE
)

enum class RiskLevel {
    SAFE,
    WARNING,
    CRITICAL
}

data class AuditLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val details: String,
    val riskLevel: RiskLevel
)

object ModerationService {
    // Audit logs for compliance and parent security review
    private val auditLogs = mutableListOf<AuditLogEntry>()

    // Prohibited words and patterns for child safety
    private val blockedKeywords = setOf(
        "hate", "stupid", "ugly", "idiot", "kill", "die", "shut up",
        "loser", "fool", "dumb", "swear", "badword", "curse", "gun", "weapon",
        "attack", "hurt", "punch", "fight", "bully", "threat", "bomb", "knife"
    )

    // Regex for phone numbers (e.g. 10 digits, +1-xxx, etc.)
    private val phonePattern = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}")

    // Regex for email addresses
    private val emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")

    // Regex for credit card / debit card numbers
    private val creditCardPattern = Pattern.compile("\\b(?:\\d[ -]*?){13,16}\\b")

    // Regex for Social Security / National ID
    private val ssnPattern = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b")

    // Regex for GPS Coordinates (lat, long)
    private val gpsPattern = Pattern.compile("[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)")

    // Regex for postal address indicators
    private val addressKeywords = listOf(
        "street", "avenue", "road", "blvd", "my address is", "i live at",
        "my house", "zipcode", "postal code", "apartment number", "my school is"
    )

    // Rate-limiting tracker: tracks last submission timestamps to prevent flood spam
    private var lastSubmissionTime: Long = 0
    private const val MIN_INTERVAL_MS = 1500L // Min 1.5 seconds between posts/comments

    fun screenText(input: String, strictness: String = "Strict"): ModerationResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ModerationResult(isSafe = false, violationReason = "Message cannot be empty.")
        }

        // Anti-Spam Rate Limit Check
        val now = System.currentTimeMillis()
        if (now - lastSubmissionTime < MIN_INTERVAL_MS) {
            return ModerationResult(
                isSafe = false,
                violationReason = "Please wait a moment before sending another message (Anti-Spam Protection).",
                riskLevel = RiskLevel.WARNING
            )
        }
        lastSubmissionTime = now

        val lower = trimmed.lowercase()

        // 1. Check for credit card or financial numbers
        if (creditCardPattern.matcher(trimmed).find()) {
            recordAudit("FINANCIAL_LEAK_BLOCKED", "Attempted sharing of credit card sequence", RiskLevel.CRITICAL)
            return ModerationResult(
                isSafe = false,
                violationReason = "Child Security Alert: Financial card numbers are never permitted.",
                riskLevel = RiskLevel.CRITICAL
            )
        }

        // 2. Check for SSN / National IDs
        if (ssnPattern.matcher(trimmed).find()) {
            recordAudit("SSN_LEAK_BLOCKED", "Attempted sharing of national ID sequence", RiskLevel.CRITICAL)
            return ModerationResult(
                isSafe = false,
                violationReason = "Child Security Alert: Identification numbers are protected and cannot be shared.",
                riskLevel = RiskLevel.CRITICAL
            )
        }

        // 3. Check for GPS coordinates
        if (gpsPattern.matcher(trimmed).find()) {
            recordAudit("LOCATION_LEAK_BLOCKED", "Attempted sharing of GPS coordinates", RiskLevel.CRITICAL)
            return ModerationResult(
                isSafe = false,
                violationReason = "Child Safety Rule: Sharing geographical GPS coordinates is strictly prohibited.",
                riskLevel = RiskLevel.CRITICAL
            )
        }

        // 4. Check for personal contact info (privacy protection)
        if (phonePattern.matcher(trimmed).find()) {
            recordAudit("PHONE_LEAK_BLOCKED", "Attempted sharing of phone number", RiskLevel.WARNING)
            return ModerationResult(
                isSafe = false,
                violationReason = "Child Safety Rule: Sharing phone numbers is strictly prohibited to protect your privacy.",
                riskLevel = RiskLevel.WARNING
            )
        }

        if (emailPattern.matcher(trimmed).find()) {
            recordAudit("EMAIL_LEAK_BLOCKED", "Attempted sharing of email address", RiskLevel.WARNING)
            return ModerationResult(
                isSafe = false,
                violationReason = "Child Safety Rule: Sharing email addresses is not allowed in public posts.",
                riskLevel = RiskLevel.WARNING
            )
        }

        for (addr in addressKeywords) {
            if (lower.contains(addr)) {
                recordAudit("ADDRESS_LEAK_BLOCKED", "Address keyword detected: $addr", RiskLevel.WARNING)
                return ModerationResult(
                    isSafe = false,
                    violationReason = "Child Safety Rule: Never share your physical location or home address online.",
                    riskLevel = RiskLevel.WARNING
                )
            }
        }

        // 5. Check for prohibited/unsafe language
        for (word in blockedKeywords) {
            if (lower.contains(word)) {
                recordAudit("HARMFUL_KEYWORD_BLOCKED", "Unkind keyword detected: $word", RiskLevel.WARNING)
                return ModerationResult(
                    isSafe = false,
                    violationReason = "Friendly Words Only: Let's keep our community kind and respectful! Please remove '$word'.",
                    riskLevel = RiskLevel.WARNING
                )
            }
        }

        // 6. Child-safe friendly auto-sanitization (sanitize repeated letters or exclamation spam)
        val sanitized = trimmed.replace(Regex("!{3,}"), "!!")

        return ModerationResult(
            isSafe = true,
            violationReason = null,
            sanitizedText = sanitized,
            riskLevel = RiskLevel.SAFE
        )
    }

    private fun recordAudit(eventType: String, details: String, risk: RiskLevel) {
        synchronized(auditLogs) {
            auditLogs.add(AuditLogEntry(eventType = eventType, details = details, riskLevel = risk))
            if (auditLogs.size > 200) {
                auditLogs.removeAt(0)
            }
        }
    }

    fun getAuditLogs(): List<AuditLogEntry> {
        return synchronized(auditLogs) { auditLogs.toList() }
    }
}
