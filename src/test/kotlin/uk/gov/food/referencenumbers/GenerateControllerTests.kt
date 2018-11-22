package uk.gov.food.referencenumbers

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class GenerateControllerTests {

    @Autowired
    lateinit var controller : GenerateController

    @Test
    fun ShouldPreventBadWordsBeingGenerated() {
        assertTrue(controller.ContainsBadWord("JEWB6L-9DBNND-M14W5G"))
        assertTrue(controller.ContainsBadWord("J3WB6L-9DBNND-M14W5G"))
        assertTrue(controller.ContainsBadWord("CG1BJE-WDBNND-M14W5G"))
    }

    @Test
    fun ShouldAllowGoodWordsToBeGenerated() {
        assertFalse(controller.ContainsBadWord("CG1B6L-9DBNND-M14W5G"))
        assertFalse(controller.ContainsBadWord("JA55M9-G4WEXE-V2204L"))
        assertFalse(controller.ContainsBadWord("C5GX1F-73WE46-Q52D89"))
    }
}
