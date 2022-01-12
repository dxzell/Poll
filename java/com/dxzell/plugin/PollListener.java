package com.dxzell.plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Text;

import java.lang.reflect.InvocationTargetException;

public class PollListener implements Listener {
    private Main main;

    public PollListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInventarClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        if (!main.getGuiListe().isEmpty() && main.getGuiListe().contains(e.getInventory())) {
            e.setCancelled(true);
            if (main.getEsWirdPollGebaut()) {

                if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.PAPER)) {
                    player.sendMessage(org.bukkit.ChatColor.GOLD + "[Poll-Builder] " + org.bukkit.ChatColor.AQUA + "Bitte schreibe eine Frage für die Umfrage in den Chat und bestätige diese, indem du auf " + org.bukkit.ChatColor.GREEN + "[Ja]" + org.bukkit.ChatColor.AQUA + " klickst!");
                    main.oeffneFrageGui(player);
                    main.setzeStartStack();

                    player.closeInventory();
                } else if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.OAK_SIGN)) {
                    player.sendMessage(ChatColor.GOLD + "[Poll-Builder] " + ChatColor.AQUA + "Gib nun die beiden Antwortmöglichkeiten der Umfrage in den Chat ein und bestätige diese, indem du auf " + org.bukkit.ChatColor.GREEN + "[Ja]" + org.bukkit.ChatColor.AQUA + " klickst!");
                    main.oeffneAntwortmoeglichkeitenGui(player);
                    main.setzeStartStack();
                    player.closeInventory();
                } else if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.GREEN_DYE) || e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.RED_DYE)) {
                    main.setzeStartStack();
                    main.setzeZugriff();
                } else if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.CLOCK)) {
                    main.setzeStartStack();
                    if (e.getInventory().equals(main.getGuiListe().get(1))) {
                        player.closeInventory();

                        int index = 0;
                        String name = e.getCurrentItem().getItemMeta().getDisplayName();

                        if (e.getCurrentItem().getItemMeta().getDisplayName().length() == 14) {
                            String[] aufgeteilt = name.split(" ");
                            System.out.println(aufgeteilt[0]);
                            StringBuilder b = new StringBuilder();
                            b.append(aufgeteilt[0].charAt(2));
                            b.append(aufgeteilt[0].charAt(3));
                            index = Integer.parseInt(b.toString());
                            System.out.println(index);
                        } else if (e.getCurrentItem().getItemMeta().getDisplayName().length() == 15) {
                            if (name.charAt(2) == '1') {
                                index = 60;
                            } else {
                                index = 120;

                            }


                        } else {
                            index = 90;
                        }

                        main.setzeZeit(index);
                        player.openInventory(main.getGuiListe().get(0));


                    } else {

                        main.oeffneZeitGui(player);

                    }
                } else if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.GOLD_BLOCK)) {
                    main.starteUmfrage(player);
                    main.setzePollLaeuft();

                } else if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.GREEN_WOOL)) {
                    main.ueberpruefePoll();
                }
            }


            if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.GREEN_CONCRETE)) {
                if (!main.getSchonGevotet().contains(player)) {
                    main.erhoeheVotesErsteAntwort();
                    main.addSchonGevotet(player);
                    main.updateStatusGui();
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.GOLD + "[Poll] " + ChatColor.RED + "Du hast deine Stimme bereits abgegeben!");
                }
            } else if (e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.RED_CONCRETE)) {
                if (!main.getSchonGevotet().contains(player)) {
                    main.erhoheVotesZweiteAntwort();
                    main.addSchonGevotet(player);
                    main.updateStatusGui();
                    player.closeInventory();
                } else {
                    player.sendMessage(ChatColor.GOLD + "[Poll] " + ChatColor.RED + "Du hast deine Stimme bereits abgegeben!");
                }
            }
        }else if(main.getErgebnisGui() != null && e.getInventory().equals(main.getErgebnisGui()))
        {
            e.setCancelled(true);
        }






    }
    @EventHandler
    public void onChat(PlayerChatEvent e)
    {
        if(e.getPlayer().equals(main.getAktSpieler())) {
            if (main.getNutzen().equalsIgnoreCase("antworten")) {
                e.setCancelled(true);
                int stufe = main.getFrageStufe();
                if (stufe == 1) {

                    TextComponent ok = new TextComponent(ChatColor.GOLD + "[Poll-Builder] ");
                    TextComponent okText = new TextComponent(ChatColor.AQUA + "Soll ");
                    TextComponent okTextZwei;
                    if(e.getMessage().contains("$")) {
                       okTextZwei = new TextComponent(ChatColor.translateAlternateColorCodes('$', e.getMessage()));
                    }else{
                        okTextZwei = new TextComponent(ChatColor.GOLD + e.getMessage());
                    }
                    TextComponent okTextDrei = new TextComponent(ChatColor.AQUA + " die erste Antwortmöglichkeit für die Umfrage sein? Wenn dem so ist, drücke auf ");
                    TextComponent okTextVier = new TextComponent(ChatColor.GREEN + "[Ja]");
                    okTextVier.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ok"));

                    ok.addExtra(okText);
                    ok.addExtra(okTextZwei);
                    ok.addExtra(okTextDrei);
                    ok.addExtra(okTextVier);

                    e.getPlayer().spigot().sendMessage(ok);
                    main.setErsteAntwort(e.getMessage());



                } else if (stufe == 2) {
                    TextComponent ok = new TextComponent(ChatColor.GOLD + "[Poll-Builder] ");
                    TextComponent okText = new TextComponent(ChatColor.AQUA + "Soll ");
                    TextComponent okTextZwei;
                    if(e.getMessage().contains("$")) {
                        okTextZwei = new TextComponent(ChatColor.translateAlternateColorCodes('$', e.getMessage()));
                    }else{
                        okTextZwei = new TextComponent(ChatColor.GOLD + e.getMessage());
                    }

                    TextComponent okTextDrei = new TextComponent(ChatColor.AQUA + " die zweite Antwortmöglichkeit für die Umfrage sein? Wenn dem so ist, drücke auf ");
                    TextComponent okTextVier = new TextComponent(ChatColor.GREEN + "[Ja]");


                    okTextVier.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ok"));
                    ok.addExtra(okText);
                    ok.addExtra(okTextZwei);
                    ok.addExtra(okTextDrei);
                    ok.addExtra(okTextVier);

                    e.getPlayer().spigot().sendMessage(ok);
                    main.setZweiteAntwort(e.getMessage());

                }
            } else if (main.getNutzen().equalsIgnoreCase("frage") && main.getFrageEingegebn() == false) {
                e.setCancelled(true);
                TextComponent ok = new TextComponent(ChatColor.GOLD + "[Poll-Builder] ");
                TextComponent okText = new TextComponent(ChatColor.AQUA + "Soll ");
                TextComponent okTextZwei;
                if(e.getMessage().contains("$")) {
                    okTextZwei = new TextComponent(ChatColor.translateAlternateColorCodes('$', e.getMessage()));
                }else{
                    okTextZwei = new TextComponent(ChatColor.GOLD + e.getMessage());
                }

                TextComponent okTextDrei = new TextComponent(ChatColor.AQUA + " die Frage für die Umfrage sein? Wenn dem so ist, drücke auf ");
                TextComponent okTextVier = new TextComponent("[Ja]");
                okTextVier.setColor(ChatColor.GREEN);
                okTextVier.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ok"));
                ok.addExtra(okText);
                ok.addExtra(okTextZwei);
                ok.addExtra(okTextDrei);
                ok.addExtra(okTextVier);
                main.setFrage(e.getMessage());

                e.getPlayer().spigot().sendMessage(ok);

            }
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e)
    {
        if(!main.getGuiListe().isEmpty() && e.getInventory().equals(main.getGuiListe().get(0)) ||!main.getGuiListe().isEmpty() &&  e.getInventory().equals(main.getGuiListe().get(1)))
        {
            e.getPlayer().getInventory().clear();
            main.setzeInventarVonSpielerZurueck((Player) e.getPlayer());
        }
    }
    @EventHandler
    public void onOpen(InventoryOpenEvent e)
    {
        if(!main.getGuiListe().isEmpty() && e.getInventory().equals(main.getGuiListe().get(0)) ||!main.getGuiListe().isEmpty() && e.getInventory().equals(main.getGuiListe().get(1)))
        {
            Player player = (Player) e.getPlayer();

            main.setzeZwischenSpeicher(e.getPlayer().getInventory().getContents());
            for(int i = 0; i < player.getInventory().getSize() - 5; i++)
            {
                player.getInventory().setItem(i, main.bauItem(org.bukkit.ChatColor.RED + " ", " ", Material.BARRIER));
            }
        }
    }







}
