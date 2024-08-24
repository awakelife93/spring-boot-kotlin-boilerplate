package com.example.demo.user.batch.config

import com.example.demo.user.batch.mapper.DeleteUserItem
import com.example.demo.user.batch.mapper.DeleteUserItemRowMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import java.util.*
import javax.sql.DataSource

@Configuration
class DeleteUserConfig(
  private val jdbcTemplate: JdbcTemplate,
  private val dataSource: DataSource
) : DefaultBatchConfiguration() {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private val chunkSize = 10

  @Bean
  fun deleteUser(
    jobRepository: JobRepository,
    transactionManager: PlatformTransactionManager
  ): Job {
    return JobBuilder("deleteUserJob", jobRepository)
      .flow(generateStep(jobRepository, transactionManager))
      .end()
      .build()
  }

  @Bean
  @JobScope
  @Throws(
    Exception::class
  )
  fun generateStep(
    jobRepository: JobRepository,
    transactionManager: PlatformTransactionManager
  ): Step {
    return StepBuilder("deleteUserStep", jobRepository)
      .chunk<DeleteUserItem, DeleteUserItem>(chunkSize, transactionManager)
      .allowStartIfComplete(true)
      .reader(reader(null))
      .writer(writer())
      .build()
  }

  @Bean
  @StepScope
  fun reader(
    @Value("#{jobParameters[now]}") now: LocalDateTime?
  ): JdbcPagingItemReader<DeleteUserItem> {
    val nowDateTime = checkNotNull(now) { "now parameter is required" }

    return JdbcPagingItemReaderBuilder<DeleteUserItem>()
      .name("DeletedUsersYearAgoReader")
      .dataSource(dataSource)
      .pageSize(chunkSize)
      .fetchSize(chunkSize)
      .queryProvider(pagingQueryProvider())
      .parameterValues(
        Collections.singletonMap<String, Any>("oneYearBeforeNow", nowDateTime.minusYears(1))
      )
      .rowMapper(DeleteUserItemRowMapper())
      .build()
  }

  @Bean
  @StepScope
  fun writer(): ItemWriter<DeleteUserItem> {
    return ItemWriter<DeleteUserItem> { items: Chunk<out DeleteUserItem> ->
      items.map {
        logger.info(
          "Hard Deleted User By = {} {} {} {} {}",
          it.name,
          it.email,
          it.role,
          it.deletedDt
        )

        jdbcTemplate.update(
          "DELETE FROM \"user\" WHERE user_id = ?",
          it.id
        )
      }
    }
  }

  @Bean
  @Throws(Exception::class)
  fun pagingQueryProvider(): PagingQueryProvider {
    val queryProvider = SqlPagingQueryProviderFactoryBean()

    queryProvider.setDataSource(dataSource)
    queryProvider.setSelectClause("select *")
    queryProvider.setFromClause("from \"user\"")
    queryProvider.setWhereClause("where deleted_dt <= :oneYearBeforeNow")

    val sortKeys: MutableMap<String, Order> = HashMap(1)
    sortKeys["user_id"] = Order.ASCENDING

    queryProvider.setSortKeys(sortKeys)

    return queryProvider.getObject()
  }
}
