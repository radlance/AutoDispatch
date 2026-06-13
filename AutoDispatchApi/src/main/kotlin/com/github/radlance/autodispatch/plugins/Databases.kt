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
import org.postgresql.Driver

fun Application.configureDatabases(config: ApplicationConfig) {
    val url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/auto_dispatch_api"
    val user = config.property("database.user").getString()
    val password = config.property("database.password").getString()

    log.info("Connecting to postgres database at $url")

    // Explicitly use the PostgreSQL Driver class to register it
    val driver = Driver()
    val props = java.util.Properties()
    props.setProperty("user", user)
    props.setProperty("password", password)
    val connection = driver.connect(url, props) ?: throw java.sql.SQLException("Could not connect to database")

    Database.connect(
        { connection }
    )

    runLiquibaseMigrations(connection)
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
