package dev.kastro.application.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("aws.sqs")
class SqsConfig {
    var transferQueue: String? = "transfer-queue"
}
