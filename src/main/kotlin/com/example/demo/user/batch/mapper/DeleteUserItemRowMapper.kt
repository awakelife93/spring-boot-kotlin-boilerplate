package com.example.demo.user.batch.mapper

import com.example.demo.utils.ConvertUtils
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class DeleteUserItemRowMapper : RowMapper<DeleteUserItem> {
  @Throws(SQLException::class)
  override fun mapRow(resultSet: ResultSet, rowNumber: Int): DeleteUserItem {
    val user = DeleteUserItem.of(
      resultSet.getLong("user_id"),
      resultSet.getString("email"),
      resultSet.getString("name"),
      resultSet.getString("role"),
      ConvertUtils.convertStringToLocalDateTimeFormat(
        resultSet.getString("deleted_dt"),
        "yyyy-MM-dd HH:mm:ss"
      )
    )

    return user
  }
}
