package pl.alorenc.etesco.tescobot.adapters.http

import org.jsoup.Jsoup
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import pl.alorenc.etesco.tescobot.adapters.http.requests.LoginRequestBody
import pl.alorenc.etesco.tescobot.adapters.http.responses.DeliveryResponseBody
import pl.alorenc.etesco.tescobot.domain.entities.Delivery
import pl.alorenc.etesco.tescobot.domain.ports.TescoClient
import java.net.HttpCookie
import java.net.URI

class TescoServiceUnavailableException : Exception()

class TescoHttpClient(
    private val email: String,
    private val password: String,
    private val restTemplate: RestTemplate,
    private val baseUrl: String
) : TescoClient {

    override fun getAllDeliveries(): List<Delivery> {
        val loginPage = getLoginPage()
        val loginPageHtml = loginPage.body ?: throw TescoServiceUnavailableException()

        val cookieCsrfToken = extractCookieCsrfToken(loginPage.headers[HttpHeaders.SET_COOKIE])
        val formCsrfToken = extractFormCsrfToken(loginPageHtml)

        val loginResponseEntity = login(email, password, cookieCsrfToken, formCsrfToken)
        val tescoServiceCookies = buildCookieStringFromResponseEntity(loginResponseEntity)

        val deliveries = fetchDeliveries(tescoServiceCookies)

        return deliveries.map { delivery ->
            Delivery(
                status = Delivery.Status.valueOf(delivery.status.toUpperCase()),
                start = delivery.start,
                end = delivery.end
            )
        }
    }

    private fun <T> buildCookieStringFromResponseEntity(responseEntity: ResponseEntity<T>): String {
        val setCookies =
            (responseEntity.headers[HttpHeaders.SET_COOKIE] ?: emptyList()).map { HttpCookie.parse(it) }

        return setCookies.flatten().joinToString("; ") { it.toString() }
    }

    private fun fetchDeliveries(cookie: String): List<DeliveryResponseBody.Delivery> {
        val deliveryDateLinks = getDeliveryDateLinks(cookie)
        return deliveryDateLinks.flatMap { getDeliveriesForDate(cookie, it) }.distinct()
    }

    private fun getDeliveriesForDate(cookie: String, dateLink: String): List<DeliveryResponseBody.Delivery> {
        val httpHeaders = HttpHeaders().apply {
            add(HttpHeaders.COOKIE, cookie)
        }

        val httpEntity = HttpEntity<MultiValueMap<String, String>>(httpHeaders)

        return restTemplate.exchange(
            URI.create("${baseUrl}$dateLink"),
            HttpMethod.GET,
            httpEntity,
            DeliveryResponseBody::class.java
        ).body?.deliveries ?: throw TescoServiceUnavailableException()
    }

    private fun getDeliveryDateLinks(cookie: String): List<String> {
        val httpHeaders = HttpHeaders().apply {
            add(HttpHeaders.COOKIE, cookie)
            add(HttpHeaders.ACCEPT, "text/html")
        }

        val httpEntity = HttpEntity<MultiValueMap<String, String>>(httpHeaders)

        val html = restTemplate.exchange(
            URI.create("${baseUrl}groceries/en-GB/slots/delivery/"),
            HttpMethod.GET,
            httpEntity,
            String::class.java
        ).body ?: throw TescoServiceUnavailableException()

        val dateLinkElements = Jsoup
            .parse(html)
            .select("a.slot-selector--week-tabheader-link")

        return dateLinkElements.eachAttr("href")
    }

    private fun login(
        email: String,
        password: String,
        cookieCsrfToken: String,
        formCsrfToken: String
    ): ResponseEntity<String> {
        val httpHeaders = HttpHeaders().apply {
            add(HttpHeaders.COOKIE, "_csrf=$cookieCsrfToken")
        }

        val request = LoginRequestBody(
            onSuccessUrl = "${baseUrl}groceries/en-GB/",
            email = email,
            password = password,
            csrf = formCsrfToken
        )

        val httpEntity = HttpEntity(request, httpHeaders)

        return restTemplate.postForEntity(
            URI.create("${baseUrl}groceries/en-GB/login"),
            httpEntity,
            String::class.java
        )
    }

    private fun getLoginPage() =
        restTemplate.getForEntity(URI.create("${baseUrl}groceries/"), String::class.java)

    private fun extractCookieCsrfToken(setCookieHeaders: List<String>?): String {
        val httpCookies = (setCookieHeaders ?: emptyList()).map { HttpCookie.parse(it) }
        return httpCookies.flatten().first { it.name == "_csrf" }.value
    }

    private fun extractFormCsrfToken(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.select("input[name=_csrf]").first().`val`()
    }
}