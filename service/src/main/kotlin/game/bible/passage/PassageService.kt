package com.kotlin.multimodulespringboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PassageService

fun main(args: Array<String>) {
	runApplication<PassageService>(*args)
}
