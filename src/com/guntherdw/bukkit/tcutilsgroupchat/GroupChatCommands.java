package com.guntherdw.bukkit.tcutilsgroupchat;

import com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Commands.Commands.ChatCommands;
import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * @author Edoxile, GuntherDW
 */
public class GroupChatCommands {

    private TweakcraftUtils tcutils;
    private GroupChatPlugin plugin;

    public GroupChatCommands(GroupChatPlugin instance) {
        this.plugin = instance;
        this.tcutils = instance.tcutilsInstance;
    }

    @aCommand(
        aliases = {"gr"},
        permissionBase = "chat.mode.group",
        description = "Group chat options",
        section = "chat"
    )
    public boolean group(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException, ChatModeException {

        if (!tcutils.getConfigHandler().enableGroupChat) {
            throw new CommandUsageException("GroupChat not enabled!");
        }

        GroupChat groupChat = null;

        try {
            ChatMode cm = tcutils.getChathandler().getChatMode("group");
            if(cm!=null && cm instanceof com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat) groupChat = (GroupChat) cm;
        } catch (ChatModeException e) {
            throw new ChatModeException("ChatMode not found, are you sure this plugin is loaded correctly?");
        }

        if(groupChat == null) {
            throw new ChatModeException("ChatMode not found, are you sure this plugin is loaded correctly?");
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                args[0] = args[0].toLowerCase();
                if (args[0].equals("create")) {
                    if (args.length > 1) {
                        String topic = "";
                        for (int i = 1; i < args.length; i++) {
                            if (i != 1)
                                topic += " ";
                            topic += args[i];
                        }
                        groupChat.create(player, topic);
                    } else {
                        throw new CommandUsageException("Too few arguments passed to /group create.");
                    }
                } else if (args[0].equals("invite")) {
                    if (args.length == 1) {
                        Player otherPlayer = tcutils.findPlayerasPlayer(args[0]);
                        if (otherPlayer != null) {
                            groupChat.addInvite(player, otherPlayer);
                        } else {
                            player.sendMessage(ChatColor.RED + "Player '" + args[0] + "' not found!");
                        }
                    } else {
                        throw new CommandUsageException("Too many arguments passed to /group invite.");
                    }
                } else if (args[0].equals("leave")) {
                    groupChat.removeRecipient(player);
                } else if (args[0].equals("topic")) {
                    if (args.length > 1) {
                        String topic = "";
                        for (int i = 1; i < args.length; i++) {
                            if (i != 1)
                                topic += " ";
                            topic += args[i];
                        }
                        groupChat.setTopic(player, topic);
                    } else {
                        throw new CommandUsageException("Too few arguments passed to /group topic");
                    }
                } else if (args[0].equals("join")) {
                    groupChat.addRecipient(player);
                } else if (args[0].equals("deny")) {
                    groupChat.declineRecipient(player);
                } else if (args[0].equals("help")) {
                    player.sendMessage("Help for /group. Still has to be implemented.");
                } else {
                    throw new CommandUsageException("Invalid command usage. See /group help");
                }
            } else {
                throw new CommandUsageException("Invalid command usage. See /group help.");
            }
        } else {
            throw new CommandSenderException("Console cannot use GroupChat.");
        }
        return true;
    }

    @aCommand(
        aliases = {"g"},
        permissionBase = "chat.mode.group",
        description = "Group chat options",
        section = "chat"
    )
    public boolean groupChat(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, ChatModeException, CommandException {

        if (!tcutils.getConfigHandler().enableGroupChat) {
            throw new CommandUsageException("GroupChat not enabled!");
        }

        GroupChat groupChat = null;

        try {
            ChatMode cm = tcutils.getChathandler().getChatMode("group");
            if(cm!=null && cm instanceof com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat) groupChat = (GroupChat) cm;
        } catch (ChatModeException e) {
            throw new ChatModeException("ChatMode not found, are you sure this plugin is loaded correctly?");
        }

        if(groupChat == null) {
            throw new ChatModeException("ChatMode not found, are you sure this plugin is loaded correctly?");
        }

        if (args.length == 0) {
            //TODO: add user to auto-groupchat
            // throw new CommandUsageException("No text entered to send to group!");
            ChatCommands chatCommands = tcutils.getCommandHandler().chatCommands;
            chatCommands.chatCommand(sender, command, "group", args);
            // return this.chatCommand();

        } else {
            String text = "";
            for (int i = 0; i < args.length; i++) {
                if (i != 0)
                    text += " ";
                text += args[i];
            }
            groupChat.sendMessage(sender, text);
        }
        return true;
    }

}
