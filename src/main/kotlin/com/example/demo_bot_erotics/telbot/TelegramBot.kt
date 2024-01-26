package com.example.demo_bot_erotics.telbot

import io.micrometer.common.util.StringUtils
import jakarta.servlet.http.HttpServletRequest
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.*
import java.util.stream.Collectors


@Component
@RestController
class TelegramBot(
    @Value("\${bot.token}") var token: String,
    @Value("\${chat.id}") var chatId: String
) : TelegramLongPollingBot(token) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TelegramBot::class.java)
    }

    override fun getBotUsername(): String {
        return "@test_vrrr_bot"
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message: Message = update.message
            val text: String = message.text

            val sendMessage = SendMessage()
            sendMessage.chatId = chatId
            sendMessage.text = text

            try {
                execute(sendMessage)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }

    @PostMapping("/")
    @ResponseBody
    fun sendMsg(
        @RequestParam(required = false) name: String?,
        @RequestParam phone: String,
        @RequestParam(required = false, name = "comments") msg: String?,
        @RequestParam(name = "vr-one", defaultValue = "false") vrOne: Boolean,
        @RequestParam(name = "vr-one-monitor", defaultValue = "false") vrOneMonitor: Boolean,
        @RequestParam(name = "vr-toy", defaultValue = "false") vrToy: Boolean,
        @RequestParam(name = "vr-my-own", defaultValue = "false") vrMyOwn: Boolean,
        @RequestParam(name = "vr-gift", defaultValue = "false") vrGift: Boolean,
        @RequestParam(name = "vr-my-own-buy", defaultValue = "false") vrMyOwnBuy: Boolean,
        @RequestParam(name = "region", defaultValue = "Москва") region: String,
        request: HttpServletRequest
    ): String {
        try {
            val map: MutableMap<String, String> = LinkedHashMap(11)
            map["Сайт: "] = request.getHeader("host")
            map["Регион: "] = region
            if (!StringUtils.isEmpty(name)) map["Имя: "] = name!!
            map["Телефон: "] = phone
            if (!StringUtils.isEmpty(msg)) map["Сообщение: "] = msg!!
            if (vrOne) map["Аренда VR шлема с контентом"] = ""
            if (vrOneMonitor) map["Добавить вывод на экран"] = ""
            if (vrToy) map["Добавить игрушку"] = ""
            if (vrMyOwn) map["Установить контент на мой шлем"] = ""
            if (vrGift) map["Подарочный сертификат"] = ""
            if (vrMyOwnBuy) map["Купить очки"] = ""

            val text = map.entries.stream()
                .map { e: Map.Entry<String, String> -> e.key + e.value }
                .collect(Collectors.joining("\n"))

            val encoding = request.getHeader("Accept-Encoding")
            if (StringUtils.isEmpty(encoding) || StringUtils.isEmpty(phone) || "Москва" == region) {
                logger.info { "Сработала защита от ботов. $text" }
            } else {

                val headersMap: Map<String, String> = Collections.list(request.headerNames)
                    .stream()
                    .collect(Collectors.toMap({ n: String -> n }, { request.getHeader(it) }))

                val sb = StringBuilder("\n")
                for (entry in headersMap.entries) {
                    sb.append(entry.key).append(": ").append(entry.value).append("\n")
                }

                val message = SendMessage()
                message.chatId = chatId
                message.text = text + sb.toString()

                try {
                    execute(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            logger.error{ "Ошибка отправки сообщения с заявкой. Имя: $name. Телефон: $phone. Сообщение: $msg$e" }
        }

        return ""
    }
}