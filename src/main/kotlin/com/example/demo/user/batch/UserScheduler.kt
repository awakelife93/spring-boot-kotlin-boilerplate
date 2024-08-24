package com.example.demo.user.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserScheduler(
  private val jobLauncher: JobLauncher,
  private val jobRegistry: JobRegistry
) {
  // 1 am
  @Scheduled(cron = "0 0 01 * * ?")
  fun run() {
    try {
      val job: Job = jobRegistry.getJob("deleteUserJob")
      val jobParameters: JobParameters = JobParametersBuilder()
        .addLocalDateTime("now", LocalDateTime.now())
        .toJobParameters()

      jobLauncher.run(job, jobParameters)
    } catch (exception: Exception) {
      when (exception) {
        is NoSuchJobException,
        is JobInstanceAlreadyCompleteException,
        is JobExecutionAlreadyRunningException,
        is JobParametersInvalidException,
        is JobRestartException -> throw RuntimeException(exception)

        else -> throw exception
      }
    }
  }
}
