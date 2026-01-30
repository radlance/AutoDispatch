package com.github.radlance.autodispatch.service

import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

class MailService {
    private val smtpPassword = System.getenv("SMTP_PASSWORD") ?: ""

    private val mailer = MailerBuilder
        .withSMTPServer("smtp.gmail.com", 465, "manyakindima@gmail.com", smtpPassword)
        .withTransportStrategy(TransportStrategy.SMTPS)
        .buildMailer()

    fun sendEmail(to: String, subject: String, body: String) {
        val email = EmailBuilder.startingBlank()
            .from("Логистика", "manyakindima@gmail.com")
            .to(to)
            .withSubject(subject)
            .withHTMLText(body)
            .buildEmail()

        mailer.sendMail(email)
    }
}