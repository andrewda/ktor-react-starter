package me.andrewda.utils

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

object Database {
    private val databaseConfig = javaClass.getResource("/db.txt")

    private val url = if (databaseConfig != null) {
        databaseConfig.readText().trim()
    } else {
        exposedLogger.warn("Could not find db.txt, falling back to default address")
        "mysql://root:root@localhost:3306/ktor-app"
    }

    private val connection = Database.connect("jdbc:$url", driver = "com.mysql.cj.jdbc.Driver")

    fun init() {
        val transaction = TransactionManager.currentOrNew(Connection.TRANSACTION_REPEATABLE_READ)

        SchemaUtils.createMissingTablesAndColumns(Users)

        transaction.commit()

        println("Database initiated.")
    }
}

object Users : Table() {
    val id = varchar("id", 10).primaryKey()
    val username = varchar("username", 20)
    val email = varchar("email", 50)
    val name = varchar("name", 50)
}
