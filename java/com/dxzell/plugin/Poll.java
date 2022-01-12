package com.dxzell.plugin;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.w3c.dom.Text;


import java.awt.*;
import java.util.List;

public class Poll {

    private String pollFrage;
    private String antwortEins;
    private String antwortZwei;
    private boolean zugriffAlle;
    private int abstimmungszeit;
    private Player ersteller;
    private Main main;
    private int runnable;
    private int aktZeit;

    public Poll(Player ersteller, String pollFrage, boolean zugriffAlle, int abstimmungszeit, String antwortEins, String antwortZwei, Main main)
    {

        this.pollFrage = pollFrage;
        this.zugriffAlle = zugriffAlle;
        this.abstimmungszeit = abstimmungszeit;
        this.ersteller = ersteller;
        this.main = main;
        System.out.println(pollFrage);
        sendeAbstimmungAnSpieler(false);
        pollPost();

    }
    public void pollPost()
    {

        if(abstimmungszeit == 10)
        {
           startRunnable(10);
        }else if(abstimmungszeit == 15)
        {
            startRunnable(15);
        }else if(abstimmungszeit == 30)
        {
            startRunnable(30);
        }else if(abstimmungszeit == 45)
        {
            startRunnable(45);
        }else if(abstimmungszeit == 60)
        {
            startRunnable(60);
        }else if(abstimmungszeit == 90)
        {
            startRunnable(90);
        }else if(abstimmungszeit == 120)
        {
            startRunnable(120);
        }
    }
    public void startRunnable(int zeit)
    {
        aktZeit = zeit;

        runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
              aktZeit = aktZeit - 5;
                 if(aktZeit == 5) {
                     sendeAbstimmungAnSpieler(false);
                     Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                         @Override
                         public void run() {
                             sendeAbstimmungAnSpieler(true);
                             Bukkit.getScheduler().runTaskLater(main, new Runnable() {
                                 @Override
                                 public void run() {
                                     ende();
                                 }
                             }, 600L);
                         }
                     }, 5400L);
                 }else if(aktZeit == 0){

                         Bukkit.getScheduler().cancelTask(runnable);
                     }else{
                     sendeAbstimmungAnSpieler(false);
                 }


            }



        }, 6000L, 6000L);
    }
    public TextComponent abstimmungsText(Player player, String titel, String befehlText, String befehl)
    {

        TextComponent text = new TextComponent("▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n\n");   //Rand

        text.addExtra(zentriereText(titel + "$"));

        TextComponent titelText = new TextComponent(ChatColor.GOLD + titel);
        text.addExtra(titelText);

        text.addExtra("\n");

        text.addExtra(zentriereText( "$Von " +  ersteller.getName()));

        TextComponent von = new TextComponent(ChatColor.YELLOW + "Von ");       //Ersteller Name
        TextComponent erstellerName = new TextComponent(ChatColor.RED + ersteller.getName());
        von.addExtra(erstellerName);
        text.addExtra(von);
        text.addExtra("\n\n");

        text.addExtra(zentriereText(pollFrage));
        text.addExtra(ChatColor.translateAlternateColorCodes('$', pollFrage));
        text.addExtra("\n\n");

        text.addExtra(zentriereText(befehlText));

        TextComponent klickText = new TextComponent(ChatColor.YELLOW + befehlText);  //KlickText
        klickText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, befehl));
        text.addExtra(klickText);
        text.addExtra("\n");

        TextComponent textEnde = new TextComponent("\n▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒" );   //Rand
        text.addExtra(textEnde);

        return text;
    }
    public String zentriereText(String text)
    {
        int anzahlFarbCode = 0;
        for(int i = 0; i < text.length(); i++)
        {
            char akt = text.charAt(i);
            if(akt == '$') {
                anzahlFarbCode = anzahlFarbCode + 2;
            }
        }
        StringBuilder builder = new StringBuilder();
        int abstand = Math.round(((44 - text.length()) + anzahlFarbCode)/2);
        for(int i = 0; i < abstand; i++)
        {
            builder.append(" ");
        }
        return builder.toString();
    }
    public void sendeAbstimmungAnSpieler(boolean letzeChance)
    {
        if(main.getZugriff().equalsIgnoreCase("alle"))
        {
            if(!letzeChance) {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    p.spigot().sendMessage(abstimmungsText(p, "Abstimmung", "[Klicke zum Abstimmen]", "/poll"));
                }
            }else{
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    p.spigot().sendMessage(abstimmungsText(p, "Abstimmung", "[Klicke zum Abstimmen]", "/poll"));
                    p.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Die Umfrage endet in 30 Sekunden, nutze deine Stimme!");
                }
            }
        }else{
            if(!letzeChance) {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if(p.hasPermission("citybuild.poll")) {
                        p.spigot().sendMessage(abstimmungsText(p, "Abstimmung", "[Klicke zum Abstimmen]", "/poll"));
                    }
                }
            }else{
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if(p.hasPermission("citybuild.poll")) {
                        p.spigot().sendMessage(abstimmungsText(p, "Abstimmung", "[Klicke zum Abstimmen]", "/poll"));
                        p.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Die Umfrage endet in 30 Sekunden, nutze deine Stimme!");
                    }
                }
            }
        }

    }
    public void ende()
    {
        main.updateErgebnisGui();
        main.setErgebnisReady();
       for(Player player : Bukkit.getServer().getOnlinePlayers())
       {
           if(main.getZugriff().equalsIgnoreCase("alle")) {
               player.spigot().sendMessage(abstimmungsText(player, "Abstimmungsergebnis", "[Klicke für das Ergebnis]", "/poll ergebnis"));
           }else{
               if(player.hasPermission("citybuild.poll"))
               {
                   player.spigot().sendMessage(abstimmungsText(player, "Abstimmungsergebnis", "[Klicke für das Ergebnis]", "/poll ergebnis"));
               }
           }
       }
       main.resetAlleWerte();
    }
    public void stopRunnable()
    {
        Bukkit.getScheduler().cancelTask(runnable);
    }
    public void stoppeRunnableNormal()
    {
        Bukkit.getScheduler().cancelTask(runnable);
        main.updateErgebnisGui();
        for(Player player : Bukkit.getServer().getOnlinePlayers())
        {
            if(main.getZugriff().equalsIgnoreCase("alle")) {
                player.spigot().sendMessage(abstimmungsText(player, "Abstimmungsergebnis", "[Klicke für das Ergebnis]", "/poll ergebnis"));
            }else{
                if(player.hasPermission("citybuild.poll"))
                {
                    player.spigot().sendMessage(abstimmungsText(player, "Abstimmungsergebnis", "[Klicke für das Ergebnis]", "/poll ergebnis"));
                }
            }
        }
    }



}
