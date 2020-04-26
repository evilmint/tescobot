package pl.alorenc.etesco.tescobot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("pl.alorenc.etesco.tescobot")
class TescobotApplication

fun main(args: Array<String>) {
    runApplication<TescobotApplication>(*args)
}
