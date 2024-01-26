package com.example.demo_bot_erotics.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import com.example.demo_bot_erotics.telbot.TelegramBot

@Configuration
class BotConfig {

    @Bean
    fun telegramBotsApi(telegramBot: TelegramBot): TelegramBotsApi {
        val api = TelegramBotsApi(DefaultBotSession::class.java)
        api.registerBot(telegramBot)
        return api
    }
}