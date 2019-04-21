package me.andrewda.utils

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.andrewda.models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object Database {
    private val databaseConfig = javaClass.getResource("/db.txt")

    private val url = if (databaseConfig != null) {
        databaseConfig.readText().trim()
    } else {
        exposedLogger.warn("Could not find db.txt, falling back to default address")
        "mysql://root:root@localhost:3306/ktor-app"
    }

    val ds = HikariDataSource().apply {
        jdbcUrl = "jdbc:$url"
    }

    private val connection = Database.connect(ds)

    fun init() {
        val transaction = TransactionManager.currentOrNew(Connection.TRANSACTION_REPEATABLE_READ)
        SchemaUtils.createMissingTablesAndColumns(Users)
        transaction.commit()

        println("Database initiated")
    }
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
