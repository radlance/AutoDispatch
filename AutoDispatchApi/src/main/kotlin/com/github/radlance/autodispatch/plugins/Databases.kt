package com.github.radlance.autodispatch.plugins

import io.ktor.server.application.*
import io.ktor.server.config.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases(config: ApplicationConfig) {
    val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/auto_dispatch_api"
    val user = config.property("database.user").getString()
    val password = config.property("database.password").getString()

    log.info("Connecting to postgres database at $url")

    val driver = config.property("database.driver").getString()

    Database.connect(
        url = url,
        user = user,
        password = password,
        driver = driver
    )

    runLiquibaseMigrations(DriverManager.getConnection(url, user, password))
}

private fun Application.runLiquibaseMigrations(connection: Connection) {
    try {
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(connection))

        val liquibase = Liquibase(
            "db/changelog/db.changelog-master.yaml",
            ClassLoaderResourceAccessor(),
            database
        )

        liquibase.update()
        log.info("Liquibase migrations applied successfully")
    } catch (e: Exception) {
        log.error("Liquibase migration failed", e)
        throw e
    }
}
