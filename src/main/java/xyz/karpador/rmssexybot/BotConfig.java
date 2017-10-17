package xyz.karpador.rmssexybot;

/*
 * Copyright (C) 2017 Follpvosten
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Singleton class for loading and providing config values
 * @author Follpvosten
 */
public final class BotConfig {
    private static BotConfig instance;
    
    public static BotConfig getInstance() {
        if(instance == null)
            instance = new BotConfig();
        return instance;
    }
    
    private String telegramBotToken;
    private String telegramBotName;
    
    public void init() {
        File configFile = new File("config.json");
        if(!configFile.exists()) {
            try {
                Files.copy(new File("config.example.json"), configFile);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        try {
            String fileContent = FileUtils.readFileToString(configFile, "UTF-8");
            JSONObject configJson = new JSONObject(fileContent);
            telegramBotToken = configJson.getString("botToken");
            telegramBotName = configJson.getString("botName");
        } catch(IOException | JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String getTelegramBotToken() {
        return telegramBotToken;
    }
    
    public String getTelegramBotName() {
        return telegramBotName;
    }
    
}
