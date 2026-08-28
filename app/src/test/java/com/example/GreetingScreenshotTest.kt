package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.Athlete
import com.example.ui.components.AthleteCard
import com.example.ui.theme.YoYoTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val athlete = Athlete(
      id = "1",
      name = "Silas",
      warningDistanceMeters = 840,
      warningLevel = "15.2"
    )
    composeTestRule.setContent {
      YoYoTheme {
        AthleteCard(
          athlete = athlete,
          currentLiveDistance = 840,
          currentLiveLevel = "15.2",
          onClick = {},
          onUndo = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
