package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TycoonRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Zephyr", appName)
  }

  @Test
  fun `verify initial tycoon profile and portfolio`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = TycoonRepository(context)
    val profile = repo.loadProfile()
    assertEquals("Sophia Moore", profile.name)
    assertEquals(82745.60, profile.cashBalance, 0.01)

    val investments = repo.getInitialInvestments()
    assertTrue(investments.isNotEmpty())
    assertTrue(investments.any { it.name == "Apple Inc." })
  }

  @Test
  fun `verify dealership inventory and edit persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = TycoonRepository(context)

    val cars = repo.loadDealershipCars()
    assertEquals(3, cars.size)
    assertTrue(cars.any { it.name == "BMW X5 xDrive40i" })
    assertTrue(cars.any { it.name == "Mercedes-Benz GLE 450 4MATIC" })
    assertTrue(cars.any { it.name == "Porsche Cayenne S" })

    // Test car editing
    val bmw = cars.first { it.id == "car_bmw_x5" }
    val editedBmw = bmw.copy(
      name = "BMW X5 M Competition",
      priceBuy = 142000.0,
      priceLeaseMonthly = 1950.0,
      currency = "$",
      imageUri = "img_mercedes_gle"
    )
    val updatedCars = cars.map { if (it.id == bmw.id) editedBmw else it }
    repo.saveDealershipCars(updatedCars)

    val reloadedCars = repo.loadDealershipCars()
    val reloadedBmw = reloadedCars.first { it.id == "car_bmw_x5" }
    assertEquals("BMW X5 M Competition", reloadedBmw.name)
    assertEquals(142000.0, reloadedBmw.priceBuy, 0.01)
    assertEquals(1950.0, reloadedBmw.priceLeaseMonthly, 0.01)

    // Test settings edit mode persistence
    val settings = repo.loadDealershipSettings()
    assertEquals(false, settings.isEditModeActive)
    repo.saveDealershipSettings(settings.copy(isEditModeActive = true))
    val updatedSettings = repo.loadDealershipSettings()
    assertEquals(true, updatedSettings.isEditModeActive)
  }
}
