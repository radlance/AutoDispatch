package com.github.radlance.autodispatch.repository

import com.github.radlance.autodispatch.database.table.RefreshTokenTable
import com.github.radlance.autodispatch.database.table.RoleTable
import com.github.radlance.autodispatch.database.table.UserStatusTable
import com.github.radlance.autodispatch.database.table.UserTable
import com.github.radlance.autodispatch.domain.admin.UserDetailed
import com.github.radlance.autodispatch.domain.admin.UserRole
import com.github.radlance.autodispatch.domain.admin.UserShort
import com.github.radlance.autodispatch.domain.common.Status
import com.github.radlance.autodispatch.domain.common.TablePaginatedResult
import com.github.radlance.autodispatch.domain.request.UserManagementFilters
import com.github.radlance.autodispatch.util.loggedTransaction
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnTypeAlias
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.OrOp
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.countDistinct
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.update

class AdminRepository(
    private val authRepository: AuthRepository
) {

    private fun joinBaseQuery(createdBy: Alias<UserTable>, updatedBy: Alias<UserTable>): Join =
        UserTable
            .join(createdBy, JoinType.LEFT, UserTable.createdBy, createdBy[UserTable.id])
            .join(updatedBy, JoinType.LEFT, UserTable.updatedBy, updatedBy[UserTable.id])
            .join(UserStatusTable, JoinType.LEFT, UserTable.statusId, UserStatusTable.id)
            .join(RoleTable, JoinType.LEFT, UserTable.roleId, RoleTable.id)

    private fun selectColumns(
        createdBy: Alias<UserTable>,
        updatedBy: Alias<UserTable>
    ): List<Expression<*>> = listOf(
        UserTable.id,
        UserTable.email,
        UserTable.login,
        UserTable.fullName,
        UserTable.phoneNumber,
        UserTable.avatarUrl,
        UserTable.createdAt,
        UserTable.updatedAt,
        UserTable.lastLoginAt,
        UserStatusTable.id,
        UserStatusTable.name,
        RoleTable.id,
        RoleTable.name,
        createdBy[UserTable.id].alias("created_by_id"),
        createdBy[UserTable.login].alias("created_by_login"),
        createdBy[UserTable.fullName].alias("created_by_full_name"),
        createdBy[UserTable.phoneNumber].alias("created_by_phone_number"),
        createdBy[UserTable.avatarUrl].alias("created_by_avatar_url"),
        updatedBy[UserTable.id].alias("updated_by_id"),
        updatedBy[UserTable.login].alias("updated_by_login"),
        updatedBy[UserTable.fullName].alias("updated_by_full_name"),
        updatedBy[UserTable.phoneNumber].alias("updated_by_phone_number"),
        updatedBy[UserTable.avatarUrl].alias("updated_by_avatar_url")
    )

    private fun mapUserDetailedRow(
        row: ResultRow,
        createdBy: Alias<UserTable>,
        updatedBy: Alias<UserTable>
    ): UserDetailed {

        val createdById = createdBy[UserTable.id].alias("created_by_id")
        val createdByLogin = createdBy[UserTable.login].alias("created_by_login")
        val createdByFullName = createdBy[UserTable.fullName].alias("created_by_full_name")
        val createdByPhone = createdBy[UserTable.phoneNumber].alias("created_by_phone_number")
        val createdByAvatar = createdBy[UserTable.avatarUrl].alias("created_by_avatar_url")

        val updatedById = updatedBy[UserTable.id].alias("updated_by_id")
        val updatedByLogin = updatedBy[UserTable.login].alias("updated_by_login")
        val updatedByFullName = updatedBy[UserTable.fullName].alias("updated_by_full_name")
        val updatedByPhone = updatedBy[UserTable.phoneNumber].alias("updated_by_phone_number")
        val updatedByAvatar = updatedBy[UserTable.avatarUrl].alias("updated_by_avatar_url")

        fun mapShortUser(
            idExpr: ExpressionWithColumnTypeAlias<EntityID<Int>>,
            loginExpr: ExpressionWithColumnTypeAlias<String>,
            fullNameExpr: ExpressionWithColumnTypeAlias<String>,
            phoneExpr: ExpressionWithColumnTypeAlias<String>,
            avatarExpr: ExpressionWithColumnTypeAlias<String?>
        ): UserShort? {

            val id = row.getOrNull(idExpr) ?: return null

            return UserShort(
                id = id.value,
                login = row[loginExpr],
                fullName = row[fullNameExpr],
                phoneNumber = row[phoneExpr],
                avatarUrl = row[avatarExpr]
            )
        }

        return UserDetailed(
            id = row[UserTable.id].value,
            login = row[UserTable.login],
            email = row[UserTable.email],
            fullName = row[UserTable.fullName],
            phoneNumber = row[UserTable.phoneNumber],
            avatarUrl = row[UserTable.avatarUrl],
            status = Status(
                id = row[UserStatusTable.id].value,
                name = row[UserStatusTable.name]
            ),
            role = UserRole(
                id = row[RoleTable.id].value,
                name = row[RoleTable.name]
            ),
            createdBy = mapShortUser(
                idExpr = createdById,
                loginExpr = createdByLogin,
                fullNameExpr = createdByFullName,
                phoneExpr = createdByPhone,
                avatarExpr = createdByAvatar
            ),
            updatedBy = mapShortUser(
                idExpr = updatedById,
                loginExpr = updatedByLogin,
                fullNameExpr = updatedByFullName,
                phoneExpr = updatedByPhone,
                avatarExpr = updatedByAvatar
            ),
            createdAt = row[UserTable.createdAt].toString(),
            updatedAt = row[UserTable.updatedAt]?.toString(),
            lastLoginAt = row[UserTable.lastLoginAt]?.toString()
        )
    }

    private fun buildSearchConditions(
        q: String,
        createdBy: Alias<UserTable>,
        updatedBy: Alias<UserTable>
    ): Op<Boolean> {

        val pattern = "%${q.trim().lowercase()}%"

        return OrOp(
            listOf(

                UserTable.login.lowerCase() like pattern,
                UserTable.fullName.lowerCase() like pattern,
                UserTable.phoneNumber.lowerCase() like pattern,

                UserStatusTable.name.lowerCase() like pattern,

                RoleTable.name.lowerCase() like pattern,

                createdBy[UserTable.login].lowerCase() like pattern,
                createdBy[UserTable.fullName].lowerCase() like pattern,
                createdBy[UserTable.phoneNumber].lowerCase() like pattern,

                updatedBy[UserTable.login].lowerCase() like pattern,
                updatedBy[UserTable.fullName].lowerCase() like pattern,
                updatedBy[UserTable.phoneNumber].lowerCase() like pattern
            )
        )
    }

    suspend fun users(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        statusIds: List<Int>,
        roleIds: List<Int>,
        excludeLogin: String
    ): TablePaginatedResult<UserDetailed> = loggedTransaction {
        val createdBy = UserTable.alias("created_by")
        val updatedBy = UserTable.alias("updated_by")

        val query = joinBaseQuery(createdBy, updatedBy)

        val conditions = mutableListOf<Op<Boolean>>()

        conditions += UserTable.login neq excludeLogin

        if (statusIds.isNotEmpty()) conditions += UserTable.statusId inList statusIds
        if (roleIds.isNotEmpty()) conditions += UserTable.roleId inList roleIds
        if (!searchQuery.isNullOrBlank()) conditions += buildSearchConditions(searchQuery, createdBy, updatedBy)

        val where = AndOp(conditions.ifEmpty { listOf(Op.TRUE) })

        val total = query.select(UserTable.id.countDistinct()).where(where)
            .single()[UserTable.id.countDistinct()]

        val offset = (page - 1L) * pageSize

        val items = query
            .select(selectColumns(createdBy, updatedBy))
            .where(where)
            .orderBy(Coalesce(UserTable.updatedAt, UserTable.createdAt), SortOrder.DESC_NULLS_LAST)
            .limit(pageSize)
            .offset(offset)
            .map { row -> mapUserDetailedRow(row, createdBy, updatedBy) }

        TablePaginatedResult(items = items, totalCount = total)
    }

    suspend fun filters(): UserManagementFilters = loggedTransaction {
        UserManagementFilters(
            statuses = UserStatusTable.select(
                UserStatusTable.id, UserStatusTable.name
            ).map {
                Status(
                    id = it[UserStatusTable.id].value,
                    name = it[UserStatusTable.name]
                )
            },
            roles = RoleTable.select(
                RoleTable.id,
                RoleTable.name
            ).map {
                UserRole(
                    id = it[RoleTable.id].value,
                    name = it[RoleTable.name]
                )
            }
        )
    }

    suspend fun blockUser(
        userId: Int,
        login: String
    ) {
        updateUserStatus(
            userId = userId,
            statusId = BLOCKED_STATUS_ID,
            updatedByUserLogin = login
        )
    }

    suspend fun unblockUser(
        userId: Int,
        login: String
    ) {
        updateUserStatus(
            userId = userId,
            statusId = ACTIVE_STATUS_ID,
            updatedByUserLogin = login
        )
    }

    suspend fun updateUserStatus(
        userId: Int,
        statusId: Int,
        updatedByUserLogin: String
    ) = loggedTransaction {

        val updatedByUser = authRepository.getUserByLogin(updatedByUserLogin)

        if (updatedByUser?.id?.let { userId == it } ?: true) {
            throw IllegalStateException("You cannot change your own status")
        }

        val user = UserTable
            .select(
                UserTable.id,
                UserTable.statusId
            )
            .where { UserTable.id eq userId }
            .singleOrNull()
            ?: throw NoSuchElementException("User not found")

        val currentStatusId = user[UserTable.statusId].value

        if (currentStatusId == statusId) {
            when (statusId) {
                BLOCKED_STATUS_ID -> throw IllegalStateException("User already blocked")
                ACTIVE_STATUS_ID -> throw IllegalStateException("User already active")
            }
        }

        UserTable.update({ UserTable.id eq userId }) {
            it[this.statusId] = statusId
            it[updatedBy] = updatedByUser.id
            it[updatedAt] = CurrentTimestampWithTimeZone
        }

        RefreshTokenTable.deleteWhere {
            RefreshTokenTable.userId eq userId
        }
    }

    private companion object {
        const val ACTIVE_STATUS_ID = 1
        const val BLOCKED_STATUS_ID = 2
    }
}