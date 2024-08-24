package com.example.demo.user.batch

import com.example.demo.common.config.TestBatchConfig
import com.example.demo.user.batch.config.DeleteUserConfig
import com.example.demo.user.batch.mapper.DeleteUserItemRowMapper
import com.example.demo.user.constant.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@Tag("integration-test")
@DisplayName("integration - Delete User Config Batch Test")
@SpringBootTest(classes = [DeleteUserConfig::class, TestBatchConfig::class])
@SpringBatchTest
class DeleteUserConfigIntegrationTests(
  @Autowired private val jdbcTemplate: JdbcTemplate,

  @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,

  @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils
) {
  private val defaultUserEmail = "awakelife93@gmail.com"

  private val defaultUserEncodePassword = "$2a$10\$T44NRNpbxkQ9qHbCtqQZ7O3gYfipzC0cHvOIJ/aV4PTlvJjtDl7x2\n" +  //
    ""

  private val defaultUserName = "Hyunwoo Park"

  private val defaultUserRole = UserRole.USER

  @AfterEach
  fun cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions()
  }

  @Test
  @DisplayName("DeleteUserConfig batch Integration Test")
  @Throws(
    Exception::class
  )
  fun should_AssertBatchStatusAndExitStatusAndListOfDeletedUser_when_GivenLocalDateTimeIsNowTime() {
    val now = LocalDateTime.now().withNano(0)

    jdbcTemplate.update(
      "insert into \"user\" (created_dt, updated_dt, deleted_dt, email, name, password, role) values (?, ?, ?, ?, ?, ?, ?)",
      now,
      now,
      now.minusYears(1),
      defaultUserEmail,
      defaultUserName,
      defaultUserEncodePassword,
      defaultUserRole.name
    )

    val jobParameters = JobParametersBuilder()
      .addLocalDateTime("now", LocalDateTime.now())
      .toJobParameters()

    val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

    val deleteUserItemList = jdbcTemplate.query(
      "select * from \"user\" where deleted_dt <= ?",
      DeleteUserItemRowMapper(),
      now.minusYears(1)
    )

    assertEquals(BatchStatus.COMPLETED, jobExecution.status)
    assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
    assertThat(deleteUserItemList).isEmpty()
    assertEquals(deleteUserItemList.size, 0)
  }
}
