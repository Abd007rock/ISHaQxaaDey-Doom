package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserEntity
import com.example.service.AuthManager
import com.example.service.ModerationService
import com.example.service.UserRole
import com.example.util.AppLanguage
import com.example.util.MultilingualStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("IshaQxaaDey Doom", appName)
  }

  @Test
  fun `moderation service detects safe and unsafe content`() {
    // Note: ModerationService has a rate-limit interval, so add slight pause or test sequentially
    val safeText = "I made a pretty drawing today in school!"
    val safeResult = ModerationService.screenText(safeText)
    assertTrue(safeResult.isSafe)

    Thread.sleep(1600)
    val phoneLeak = "Call me at 555-123-4567 please!"
    val phoneResult = ModerationService.screenText(phoneLeak)
    assertFalse(phoneResult.isSafe)

    Thread.sleep(1600)
    val cardLeak = "Here is 4111 2222 3333 4444 to buy gems"
    val cardResult = ModerationService.screenText(cardLeak)
    assertFalse(cardResult.isSafe)

    Thread.sleep(1600)
    val unkindText = "I hate you you are stupid"
    val unkindResult = ModerationService.screenText(unkindText)
    assertFalse(unkindResult.isSafe)
  }

  @Test
  fun `multilingual strings provides all languages`() {
    val en = MultilingualStrings.getString("app_title", AppLanguage.ENGLISH)
    val so = MultilingualStrings.getString("app_title", AppLanguage.SOMALI)
    val ar = MultilingualStrings.getString("app_title", AppLanguage.ARABIC)
    val ur = MultilingualStrings.getString("app_title", AppLanguage.URDU)

    assertEquals("IshaQxaaDey Doom", en)
    assertEquals("IshaQxaaDey Doom", so)
    assertEquals("إشاقصادي دوم", ar)
    assertEquals("اشاقصادی ڈوم", ur)
  }

  @Test
  fun `auth manager secure hashing and rbac verification`() {
    val hash1 = AuthManager.hashCredential("MyChildSecret123")
    val hash2 = AuthManager.hashCredential("MyChildSecret123")
    val hashDiff = AuthManager.hashCredential("AnotherPass456")

    assertEquals(hash1, hash2)
    assertFalse(hash1 == hashDiff)
    assertTrue(hash1.length >= 64) // SHA-256 hex string

    // Role initialization and permission verification
    val childUser = UserEntity(
      id = "test_child",
      name = "Junior",
      email = "junior@family.com",
      avatar = "🦁",
      bio = "Playing games",
      role = "child"
    )
    AuthManager.initializeSession(childUser)

    assertFalse("Child must never have admin access", AuthManager.canAccessAdmin())
    assertFalse("Child must never have parent hub access", AuthManager.canAccessParentHub())

    // Attempting unauthorized elevation without correct PIN must fail
    val elevationFailed = AuthManager.attemptRoleElevation(UserRole.ADMIN, "wrong_pin", "1234")
    assertFalse(elevationFailed)
    assertFalse(AuthManager.canAccessAdmin())

    // Elevation with correct admin PIN succeeds
    val elevationSuccess = AuthManager.attemptRoleElevation(UserRole.ADMIN, "admin99", "1234")
    assertTrue(elevationSuccess)
    assertTrue(AuthManager.canAccessAdmin())
  }
}
