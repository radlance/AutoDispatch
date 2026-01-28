package com.github.radlance.autodispatch.presentation

import kotlinx.html.*
import kotlinx.html.stream.createHTML

object EmailTemplateBuilder {

    fun customer(
        title: String,
        content: DIV.() -> Unit
    ): String = createHTML().html {
        head {
            meta { charset = "UTF-8" }
            link(
                rel = "stylesheet",
                href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap"
            )
            style { unsafe { +EmailStyles.customer() } }
        }
        body {
            div("card") {
                div("header") { h1 { +title } }
                div("content") {
                    content()
                }
                div("footer") {
                    +"Это автоматическое сообщение от AutoDispatch"
                }
            }
        }
    }


    fun driver(
        title: String,
        buttonUrl: String? = null,
        content: DIV.() -> Unit
    ): String = createHTML().html {
        head {
            meta { charset = "UTF-8" }
            link(
                rel = "stylesheet",
                href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap"
            )
            style { unsafe { +EmailStyles.driver() } }
        }
        body {
            div("card") {
                div("header") { h2 { +title } }
                div("content") {
                    content()
                    buttonUrl?.let { url ->
                        a(href = url, classes = "button") { +"Открыть заявку" }
                    }
                }
                div("footer") {
                    +"AutoDispatch • Driver System"
                }
            }
        }
    }
}