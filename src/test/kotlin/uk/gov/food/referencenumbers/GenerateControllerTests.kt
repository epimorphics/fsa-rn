package uk.gov.food.referencenumbers

import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import uk.gov.food.rn.Authority
import uk.gov.food.rn.RNException

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

    @Test
    fun ShouldGenerateReferenceNumbers() {
        assertNotNull(controller.GetReferenceNumber(Authority(1000), "100"))
        assertNotNull(controller.GetReferenceNumber(Authority(2000), "200"))
        assertNotNull(controller.GetReferenceNumber(Authority(3000), "300"))
    }

    @Test
    fun ShouldThrowRNExceptionOnInvalidAuthority() {
        try {
            controller.GetReferenceNumber(Authority(100), "001")
            fail("Expected reference number generation to fail")
        } catch (e : RNException) {
            assertEquals("Illegal identifier for authority: 100 is not in the range 1000 : 9999", e.message)
        }
        try {
            controller.GetReferenceNumber(Authority(10000), "100")
            fail("Expected reference number generation to fail")
        } catch (e : RNException) {
            assertEquals("Illegal identifier for authority: 10000 is not in the range 1000 : 9999", e.message)
        }
    }

    @Test
    fun ShouldThrowInvalidParameterExceptionOnInvalidType() {
        try {
            controller.GetReferenceNumber(Authority(1001), "1001")
            fail("Expected reference number generation to fail")
        } catch (e : InvalidParameterException) {
            assertEquals("Type parameter invalid length, example: 105", e.message)
        }
        try {
            controller.GetReferenceNumber(Authority(9999), "00")
            fail("Expected reference number generation to fail")
        } catch (e : InvalidParameterException) {
            assertEquals("Type parameter invalid length, example: 105", e.message)
        }
    }
}
