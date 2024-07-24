package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.service.subscribe.ProcessSubscribe;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final ProcessSubscribe processSubscribe;

    private final CryptoCurrencyService cryptoCurrencyService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if (arguments != null && arguments.length > 0) {
            double price = Double.parseDouble(arguments[0]);
            Long telegramId = message.getChatId();
            processSubscribe.addSubscribe(telegramId, price);
            SendMessage answer1 = new SendMessage();
            answer1.setChatId(message.getChatId());
            SendMessage answer = new SendMessage();
            answer.setChatId(message.getChatId());

            try {
                answer1.setText("Текущая цена биткоина " + TextUtil.toString(cryptoCurrencyService.getBitcoinPrice()) + " USD");
                answer.setText("Новая подписка создана на стоимость " + price + " USD");
                absSender.execute(answer1);
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Error occurred in /subscribe command", e);
            } catch (IOException e) {
                log.error("Error getPriceBitcoin", e);
            }
        }

    }
}