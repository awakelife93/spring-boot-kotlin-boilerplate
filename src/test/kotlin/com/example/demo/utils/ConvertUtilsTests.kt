package com.example.demo.utils

import com.example.demo.utils.ConvertUtils.convertLocalDateTimeToStringFormat
import com.example.demo.utils.ConvertUtils.convertStringToLocalDateTimeFormat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@ActiveProfiles("test")
@Tag("unit-test")
@DisplayName("Unit - Convert Utils Test")
@ExtendWith(
  MockitoExtension::class
)
class ConvertUtilsTests {
  private val defaultWrongPattern = "wrong_pattern"

  @Nested
  @DisplayName("Convert String to LocalDateTime Test")
  inner class ConvertStringToLocalDateTimeFormatTest {
    @Test
    @DisplayName("Convert current string datetime & current pattern to LocalDateTime")
    fun should_AssertLocalDateTime_when_GivenCurrentStringDateTimeAndCurrentPattern() {
      val stringDateTime = LocalDateTime.now().withNano(0).toString()

      val localDateTime = convertStringToLocalDateTimeFormat(
        stringDateTime,
        "yyyy-MM-dd'T'HH:mm:ss"
      )

      Assertions.assertEquals(localDateTime.javaClass, LocalDateTime::class.java)
    }

    @Test
    @DisplayName("Failed Convert current string datetime & wrong pattern to LocalDateTime")
    fun should_AssertIllegalArgumentException_when_GivenCurrentStringDateTimeAndWrongPattern() {
      Assertions.assertThrows(
        IllegalArgumentException::class.java
      ) {
        convertStringToLocalDateTimeFormat(
          LocalDateTime.now().toString(),
          defaultWrongPattern
        )
      }
    }

    @Test
    @DisplayName("Failed Convert blank string datetime & current pattern to LocalDateTime")
    fun should_AssertDateTimeParseException_when_GivenBlankStringDateTimeAndCurrentPattern() {
      Assertions.assertThrows(
        DateTimeParseException::class.java
      ) {
        convertStringToLocalDateTimeFormat(
          "",
          "yyyy-MM-dd'T'HH:mm:ss"
        )
      }
    }
  }

  @Nested
  @DisplayName("Convert LocalDateTime to String Test")
  inner class ConvertLocalDateTimeToStringFormatTest {
    @Test
    @DisplayName("Convert current local datetime & current pattern to string datetime")
    fun should_AssertStringDateTime_when_GivenCurrentLocalDateTimeAndCurrentPattern() {
      val stringDateTime = convertLocalDateTimeToStringFormat(
        LocalDateTime.now().withNano(0),
        "yyyy-MM-dd'T'HH:mm:ss"
      )

      Assertions.assertEquals(stringDateTime.javaClass, String::class.java)
    }

    @Test
    @DisplayName("Failed Convert Current local datetime & wrong pattern to string datetime")
    fun should_AssertIllegalArgumentException_when_GivenWrongLocalDateTimeOrWrongPattern() {
      Assertions.assertThrows(
        IllegalArgumentException::class.java
      ) {
        convertLocalDateTimeToStringFormat(
          LocalDateTime.now(),
          defaultWrongPattern
        )
      }
    }
  }
}
