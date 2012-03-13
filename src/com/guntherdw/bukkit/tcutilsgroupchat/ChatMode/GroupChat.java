package com.guntherdw.bukkit.tcutilsgroupchat.ChatMode;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author GuntherDW, Edoxile
 */
public class GroupChat extends ChatMode {
    private Map<Player, Integer> playerGroupHashMap = new HashMap<Player, Integer>();
    private Map<Integer, GroupChatNode> chatNodeMap = new HashMap<Integer, GroupChatNode>();
    private Map<Player, Integer> inviteMap = new HashMap<Player, Integer>();
    private TweakcraftUtils plugin;

    public GroupChat(ChatHandler instance) {
        super(instance);
        plugin = instance.getTCUtilsInstance();
        chatModeName = "GC";
    }

    @Override
    public boolean sendMessage(CommandSender sender, String message) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW + "What were you trying to do?");
            return true;
        }


        if (getRecipients(sender) != null) {
            String playerName = plugin.getPlayerColor(sender.getName(), false) + sender.getName();
            plugin.getLogger().info("GROUP: <" + playerName + "> " + message);
            super.sendMessage(sender, message);

            if (getRecipients(sender).size() < 2)
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
        } else {
            sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
        }

        return true;
    }

    public Set<Player> getRecipients(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!playerGroupHashMap.containsKey(player)) return null;

            int hash = playerGroupHashMap.get(player);
            if (hash != 0) {
                GroupChatNode chatNode = chatNodeMap.get(hash);
                if (chatNode != null) {
                    Set<Player> recipients = new HashSet<Player>();
                    recipients.addAll(chatNode.getRecipients());
                    return recipients;
                }
            }
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return null;
    }

    public boolean broadcastMessage(CommandSender sender, String message) {
        Set<Player> recipients = getRecipients(sender);
        String playerName = plugin.getPlayerColor(sender.getName(), false) + sender.getName();
        // Apparently there is already a logging feature, so no need to log it again.
        // plugin.getLogger().info("GROUP: <" + playerName + "> " + message);
        for (Player player : recipients) {
            player.sendMessage(message);
            if (recipients.size() < 2) {
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
            }
        }
        return false;
    }

    public String getDescription() {
        return "Chat in a group";
    }


    public String getColor() {
        return ChatColor.AQUA.toString();
    }

    public String getPrefix() {
        return getColor() + "GC" + ChatColor.WHITE;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfigHandler().enableGroupChat;
    }

    @Override
    public void addRecipient(String playerName) {
    }

    public void acceptRecipient(Player player) {
        if (player != null) {
            if (!inviteMap.containsKey(player))
                player.sendMessage(ChatColor.RED + "You don't have any open invites from a GroupChat yet!");
            int hash = inviteMap.get(player);
            if (hash != 0) {
                GroupChatNode chatNode = chatNodeMap.get(hash);
                if (chatNode != null) {
                    chatNode.addRecipient(player);
                    playerGroupHashMap.put(player, chatNode.hashCode());
                    String playerName = plugin.getPlayerColor(player.getName(), false) + player.getName();
                    for (Player p : chatNode.getRecipients()) {
                        p.sendMessage(playerName + ChatColor.AQUA + " was added to your GroupChat.");
                    }
                    player.sendMessage(ChatColor.AQUA + "GroupChat topic: " + chatNode.getTopic());
                    inviteMap.remove(player);
                } else {
                    player.sendMessage(ChatColor.RED + "The GroupChat that you were invited to has since been removed.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You don't have any open invites from a GroupChat yet!");
            }
        }
    }

    @Override
    public void removeRecipient(String playerName) {
    }

    public void removeRecipient(Player player) {
        if (player != null) {
            if (!playerGroupHashMap.containsKey(player)) return;
            int hash = playerGroupHashMap.get(player);
            if (hash != 0) {
                GroupChatNode chatNode = chatNodeMap.get(hash);
                if (chatNode != null) {
                    String playerName = plugin.getPlayerColor(player.getName(), false) + player.getName();
                    for (Player p : chatNode.getRecipients()) {
                        p.sendMessage(playerName + ChatColor.AQUA + " left this GroupChat.");
                    }

                    chatNode.removePlayer(player);
                    if (chatNode.isEmpty()) {
                        chatNodeMap.remove(hash);
                    }
                }
                playerGroupHashMap.remove(player);
                player.sendMessage("You were successfully removed from the GroupChat.");
            }
        }
    }

    public void addInvite(Player inviter, Player invitee) {
        int hash = playerGroupHashMap.get(inviter);
        if (hash == 0) {
            inviter.sendMessage(ChatColor.RED + "Create a GroupChat before you invite people!");
        } else {
            GroupChatNode chatNode = chatNodeMap.get(hash);
            if (chatNode != null) {
                String inviterName = plugin.getPlayerColor(inviter.getName(), false) + inviter.getName();
                String inviteeName = plugin.getPlayerColor(invitee.getName(), false) + invitee.getName();
                for (Player p : chatNode.getRecipients()) {
                    p.sendMessage(inviterName + ChatColor.AQUA + " invited " + inviteeName + ChatColor.AQUA + " to this GroupChat.");
                }
                inviteMap.put(invitee, hash);
                invitee.sendMessage(inviterName + ChatColor.AQUA + " invited you to join their GroupChat '" + chatNode.getTopic() + "'!");
                invitee.sendMessage(ChatColor.AQUA + "Use /group accept to accept and /group decline to decline.");
            } else {
                inviter.sendMessage(ChatColor.RED + "Something went terribly wrong :(.");
            }
        }
    }

    public void declineRecipient(Player player) {
        inviteMap.remove(player);
    }

    @Override
    public Set<String> getSubscribers() {
        Set<String> set = new HashSet<String>();
        for (GroupChatNode node : chatNodeMap.values()) {
            for (Player p : node.getRecipients()) {
                set.add(p.getName());
            }
        }
        return set;
    }

    public void create(Player player, String topic) {
        removeRecipient(player);
        GroupChatNode chatNode = new GroupChatNode(player, topic);
        chatNodeMap.put(chatNode.hashCode(), chatNode);
        playerGroupHashMap.put(player, chatNode.hashCode());
        player.sendMessage(ChatColor.AQUA + "GroupChat with topic '"+topic+"' successfully created!");
        player.sendMessage(ChatColor.AQUA + "You can now invite people with /group invite <name>.");
    }

    public void setTopic(Player player, String topic) {
        int hash = playerGroupHashMap.get(player);
        if (hash != 0) {
            GroupChatNode chatNode = chatNodeMap.get(hash);
            if (chatNode != null) {
                chatNode.setTopic(topic);
                for (Player p : chatNode.getRecipients()) {
                    p.sendMessage(plugin.getPlayerColor(player.getName(), false) + player.getName() + ChatColor.AQUA + " changed the topic to: " + topic);
                }
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "Something went wrong. You're probably not in a GroupChat at the moment.");
    }

    public boolean hasGroupChat(Player player) {
        return playerGroupHashMap.containsKey(player);
    }

    private class GroupChatNode {
        private String topic;
        public int hash;
        private List<Player> recipients = new ArrayList<Player>();

        public GroupChatNode(Player player, String topic) {
            this.topic = topic;
            recipients.add(player);
            hash = (Integer.toString(player.hashCode()) + topic).hashCode();
        }

        public void addRecipient(Player player) {
            recipients.add(player);
        }

        public List<Player> getRecipients() {
            return recipients;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof GroupChat && other.hashCode() == hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String newTopic) {
            topic = newTopic;
        }

        public boolean isEmpty() {
            return recipients.isEmpty();
        }

        public boolean removePlayer(Player player) {
            recipients.remove(player);
            return recipients.isEmpty();
        }
    }
}