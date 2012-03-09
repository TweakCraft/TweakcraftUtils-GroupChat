package com.guntherdw.bukkit.tcutilsgroupchat;

import com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
* @author Edoxile, GuntherDW
*/
public class GroupChatPlugin extends JavaPlugin {

    private Logger log = Logger.getLogger("Minecraft");
    protected TweakcraftUtils tcutilsInstance = null;

    private PluginDescriptionFile pdfFile;

    public void onDisable() {

    }

    public void onEnable() {
        pdfFile = this.getDescription();
        log.info("[" + pdfFile.getName() + "] Loading " + pdfFile.getName() + " version " + pdfFile.getVersion() + "!");

        tcutilsInstance = TweakcraftUtils.getInstance();
        if (tcutilsInstance == null) {
            getServer().getPluginManager().disablePlugin(this);
            log.warning("[" + pdfFile.getName() + "] Couldn't find valid TweakcraftUtils instance, disabling plugin!");
            return;
        } else {
            log.info("[" + pdfFile.getName() + "] Successfully registered with TweakcraftUtils (version " + tcutilsInstance.getVersion() + ")!");
        }

        this.registerEvents();

        this.injectChatMode();

        this.injectCommands();
    }

    public void registerEvents() {

    }

    public void injectCommands() {
        CommandHandler commandHandler = tcutilsInstance.getCommandHandler();

        commandHandler.addCommandClass(new GroupChatCommands(this));
    }

    public void injectChatMode() {
        ChatHandler chatHandler = tcutilsInstance.getChathandler();

        chatHandler.registerChatMode("group", new com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat(chatHandler));
    }

}
