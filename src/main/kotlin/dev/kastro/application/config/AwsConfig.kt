package dev.kastro.application.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("aws")
class AwsConfig {
    var accessKeyId: String? = null
    var secretKey: String? = null
    var region: String? = null
    var endpoint: String? = null
}
