/*
 * Copyright (C) 2017 Follpvosten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package xyz.karpador.rmssexybot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.net.HttpURLConnection.HTTP_OK;
import java.net.URL;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 * @author Follpvosten
 */
public class RMSPollingBot extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return BotConfig.getInstance().getTelegramBotToken();
    }

    private static final String IMAGESTART = "/img/";
    private static final String IMAGEEND = "\" height=\"100%\"></a>";
    private static final String IMAGEURLSTART = "https://rms.sexy/img/";
    
    private final HashMap<String, String> telegramResourceIDs;

    public RMSPollingBot() {
        telegramResourceIDs = new HashMap<>();
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if(update.hasMessage()) {
                if(update.getMessage().hasText()) {
                    if(update.getMessage().getText().startsWith("/rms")) {
                        URL url = new URL("https://rms.sexy/");                    
                        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                        if(con.getResponseCode() == HTTP_OK) {
                            BufferedReader br = 
                                new BufferedReader(
                                    new InputStreamReader(con.getInputStream())
                                );
                            String httpResult = "";
                            String line;
                            while((line = br.readLine()) != null)
                                httpResult += line;
                            int imageIndex = httpResult.indexOf(IMAGESTART) + IMAGESTART.length();
                            int imageLength = httpResult.indexOf(IMAGEEND);
                            String imageUrl = IMAGEURLSTART + httpResult.substring(imageIndex, imageLength);
                            if(telegramResourceIDs.containsKey(imageUrl)) {
                                SendPhoto photo = new SendPhoto()
                                                .setChatId(update.getMessage().getChatId())
                                                .setPhoto(telegramResourceIDs.get(imageUrl));
                                sendPhoto(photo);
                            } else {
                                InputStream stream = new URL(imageUrl).openStream();
                                SendPhoto photo = new SendPhoto()
                                                .setChatId(update.getMessage().getChatId())
                                                .setNewPhoto("photo", stream);
                                telegramResourceIDs.put(imageUrl, sendPhoto(photo).getPhoto().get(0).getFileId());
                            }
                        }
                    }
                }
            }
        } catch(IOException | TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BotConfig.getInstance().getTelegramBotName();
    }
    
}
