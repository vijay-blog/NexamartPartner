package com.daily.nexamartpartner

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthFlowUiTest {
    private fun resetToUnauthenticated() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        runBlocking { container.sessionManager.clearSession() }
        container.authStateStore.setUnauthenticated()
    }

    @Test
    fun freshLaunchShowsLogin() {
        resetToUnauthenticated()
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(900)

        onView(withId(R.id.identifierInputEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordInputEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun loginValidationShowsErrors() {
        resetToUnauthenticated()
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(900)

        onView(withId(R.id.loginButton)).perform(click())
        onView(withText("Please enter your phone or email.")).check(matches(isDisplayed()))
        onView(withText("Please enter your password.")).check(matches(isDisplayed()))
    }

    @Test
    fun roleRoutingNavigatesToExpectedPlaceholders() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        onView(withText("NexaMart Admin")).check(matches(isDisplayed()))

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.DELIVERY_PARTNER))
        }
        onView(withText("NexaMart Delivery")).check(matches(isDisplayed()))

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setUnsupportedRole(
                "This account does not have permission to use the NexaMart Admin & Delivery application."
            )
        }
        onView(withText("Access Restricted")).check(matches(isDisplayed()))
    }

    @Test
    fun logoutReturnsToLogin() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        onView(withText("NexaMart Admin")).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    private fun testSession(role: UserRole): UserSession {
        return UserSession(
            accessToken = "access",
            refreshToken = "refresh",
            userId = 1L,
            name = "Test",
            contact = "9999999999",
            role = role
        )
    }
}
