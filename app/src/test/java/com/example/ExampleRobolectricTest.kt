package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("ChemMaster AI", appName)
  }

  @Test
  fun `verify chemistry knowledge base and RAG search`() {
    val topics = com.example.data.knowledge.ChemistryKnowledgeBase.topics
    assertTrue(topics.isNotEmpty())
    val redoxTopic = topics.find { it.id == "redox_balancing" }
    assertNotNull(redoxTopic)

    val searchResult = com.example.data.knowledge.ChemistryKnowledgeBase.searchCurriculum("electrolysis")
    assertTrue(searchResult.contains("Electrolysis", ignoreCase = true))
  }

  @Test
  fun `verify 3D molecule repository`() {
    val molecules = com.example.ui.screens.molecules.MoleculeRepository.molecules
    assertTrue(molecules.size >= 8)
    val benzene = molecules.find { it.id == "benzene" }
    assertNotNull(benzene)
    assertEquals(12, benzene?.atoms?.size) // 6 carbons + 6 hydrogens
  }
}
