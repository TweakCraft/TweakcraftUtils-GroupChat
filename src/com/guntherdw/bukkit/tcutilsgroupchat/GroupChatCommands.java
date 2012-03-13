package com.guntherdw.bukkit.tcutilsgroupchat;

import com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Commands.Commands.ChatCommands;
import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

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
        aliases = {"gr", "group"},
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
            if (cm != null && cm instanceof com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat)
                groupChat = (GroupChat) cm;
        } catch (ChatModeException e) {
            throw new ChatModeException("ChatMode not found, are you sure this plugin is loaded correctly?");
        }

        if (groupChat == null) {
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
                    if (args.length == 2) {
                        Player otherPlayer = tcutils.findPlayerasPlayer(args[1]);
                        if (otherPlayer != null) {
                            groupChat.addInvite(player, otherPlayer);
                        } else {
                            player.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found!");
                        }
                    } else {
                        throw new CommandUsageException("Too many arguments passed to /group invite.");
                    }
                } else if (args[0].equals("leave")) {
                    groupChat.removeRecipient(player);
                } else if (args[0].equals("list")) {
                    Set<Player> players = groupChat.getRecipients(player);
                    if(players == null || players.isEmpty()){
                        player.sendMessage(ChatColor.RED + "You're not in a group yet!");
                    } else {
                        String list = "";
                        for(Player p : players){
                            list += tcutils.getPlayerColor(p.getName(), false) + p.getName() + ChatColor.AQUA + ", ";
                        }
                        list = list.substring(0, list.length() - 2);
                        player.sendMessage(ChatColor.AQUA + "Currently in your ChatGroup '" + groupChat.getTopic(player) + "':");
                        player.sendMessage(list);
                    }
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
                        player.sendMessage(ChatColor.AQUA + "Topic: " + groupChat.getTopic(player));
                    }
                } else if (args[0].equals("accept")) {
                    groupChat.acceptRecipient(player);
                } else if (args[0].equals("decline")) {
                    groupChat.declineRecipient(player);
                } else if (args[0].equals("help")) {
                    player.sendMessage(ChatColor.AQUA + "Help for /group. If you want to send some chat use /g or /groupchat.");
                    player.sendMessage("/group create <topic> - create a group (topic obligated)");
                    player.sendMessage("/group invite <player> - invite a player to your group");
                    player.sendMessage("/group accept - accept a group invitation");
                    player.sendMessage("/group decline - decline a group invitation");
                    player.sendMessage("/group leave - leave your current group");
                    player.sendMessage("/group list - List all the people inside your group");
                    player.sendMessage("/group topic - view or change the topic of your group");
                } else {
                    throw new CommandUsageException(ChatColor.RED + "Invalid command usage. See /group help");
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
            if (cm != null && cm instanceof com.guntherdw.bukkit.tcutilsgroupchat.ChatMode.GroupChat)
                groupChat = (GroupChat) cm;
        } catch (ChatModeException e) {
            throw new ChatModeException("ChatMode not found, are you sure this plugin is loaded correctly?");
        }

        if (groupChat == null) {
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
