package com.dxzell.plugin;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public final class Main extends JavaPlugin {

    private boolean erstelltGeradeEinPoll;
    private List<Inventory> guiListe;
    private int stufe;
    private Player aktSpieler; // derjenige, der gerade ein Poll erzeugt

    private String ersteAntwort;
    private String zweiteAntwort;
    private String frage;
    private String zugriff;
    private int zeit;
    private Poll umfrage;
    private ItemStack zwischenSpeicher[];
    private int votesErsteAntwort;
    private int votesZweiteAntwort;
    private int startZaehler;
    private int runnable;
    private String nutzen;

    private int[] zeiten;
    private boolean frageEingegeben;
    private boolean antwortEingegeben;
    private boolean esWirdPollGebaut;
    private boolean esLaueftGeradeEinPoll;
    private List<Player> schonGevotet;
    private Inventory ergebnisGui;
    private boolean ergebnisReady;


    @Override
    public void onEnable() {

        System.out.println("Poll Plugin hat geladen!");
        guiListe = new ArrayList<>();
        stufe = 1;
        zeiten = new int[]{10, 15, 30, 45, 60, 90, 120};
        zeit = 0;
        frage = "";
        ersteAntwort = "Ja";
        zweiteAntwort = "Nein";
        zugriff = "Alle";
        frageEingegeben = false;
        nutzen = "";
        votesErsteAntwort = 0;
        votesZweiteAntwort = 0;
        schonGevotet = new ArrayList<>();
        esWirdPollGebaut = false;
        esLaueftGeradeEinPoll = false;
        ergebnisReady = false;

        getCommand("Poll").setExecutor(new PollCommands(this));
        getCommand("ok").setExecutor(new OkCommand(this));
        getCommand("Poll").setTabCompleter(new PollTab());

        Bukkit.getPluginManager().registerEvents(new PollListener(this), this);

    }

    public void baueCreateGui(Player player) { //Grund-Gui für das Erstellen der Abstimmungen
        Inventory createGui = Bukkit.createInventory(player, 36, ChatColor.BLACK + ">> " + ChatColor.WHITE + "Poll " + ChatColor.BLACK + "create");
        aktSpieler = player;
        esWirdPollGebaut = true;
        ergebnisReady = false;


        for (int i = 0; i < createGui.getSize(); i++) {
            ItemStack fuellGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta fuellGlassMeta = fuellGlass.getItemMeta();
            fuellGlassMeta.setDisplayName(" ");
            fuellGlassMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            fuellGlassMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            fuellGlass.setItemMeta(fuellGlassMeta);
            createGui.setItem(i, fuellGlass);

        }

        // CREATE GUI ITEMS
        ItemStack schild = bauItem("§8» §cAntwortmöglichkeiten", "\n°§eGebe hier mit die Antwortmöglichkeiten°" +
                "§efür deine Abstimmung an, Standardmäßig°" +
                "§esind diese auf §aJa §eund §cNein°", Material.OAK_SIGN);
        createGui.setItem(10, schild);

        ItemStack paper = bauItem("§8» §dAbstimmungsfrage", "\n°" + ChatColor.YELLOW + "Gebe hier die Frage ein, die du°" + ChatColor.YELLOW + "bei deiner Abstimmung fragen willst", Material.PAPER);
        createGui.setItem(12, paper);

        ItemStack gruenFarbe = bauItem("§8» §cZugriff", "\n°" + ChatColor.YELLOW + "Alle", Material.GREEN_DYE);
        createGui.setItem(14, gruenFarbe);

        ItemStack uhr = bauItem("§8» §bAbstimmungszeit", "\n°" + ChatColor.GREEN + "Dauer »", Material.CLOCK);
        createGui.setItem(16, uhr);

        ItemStack start = bauItem("§2» §2Start", "\n°" + ChatColor.YELLOW + "Starte hiermit die Abstimmung, wenn°" + ChatColor.YELLOW + "du zufrieden mit deinen Einstellungen°" + ChatColor.YELLOW + "bist", Material.GREEN_WOOL);
        createGui.setItem(31, start);

        guiListe.add(createGui); //0
        player.openInventory(createGui);

        // ENDE

        //ZeitGui

        Inventory zeitGui = Bukkit.createInventory(player, 9, ChatColor.GRAY + ">>" + ChatColor.BLUE + " Stelle die Abstimmungszeit ein");

        for (int k = 0; k < 7; k++) {
            ItemStack zeit = new ItemStack(Material.CLOCK);
            ItemMeta zeitMeta = zeit.getItemMeta();
            int stunden = zeiten[k] / 60;
            int minuten = zeiten[k];

            if (stunden >= 1) {
                minuten = minuten - (60 * stunden);
                if (minuten > 0) {
                    zeitMeta.setDisplayName(ChatColor.AQUA + String.valueOf(stunden) + ChatColor.AQUA + " Stunde(n) und " + ChatColor.AQUA + String.valueOf(minuten) + ChatColor.AQUA + " Minuten");
                } else {
                    zeitMeta.setDisplayName(ChatColor.AQUA + String.valueOf(stunden) + ChatColor.AQUA + " Stunde(n)");

                }

            } else {
                zeitMeta.setDisplayName(ChatColor.AQUA + String.valueOf(minuten) + ChatColor.AQUA + " Minuten");
            }

            zeit.setItemMeta(zeitMeta);

            zeitGui.setItem(k + 1, zeit);

            guiListe.add(zeitGui); //1

            //ENDE


            //AbstimmungGUI

            Inventory abstimmungGUI = Bukkit.createInventory(player, 27, ChatColor.GOLD + "Abstimmung");

            for (int j = 0; j < 27; j++) {
                abstimmungGUI.setItem(j, bauItem(" ", " ", Material.BLACK_STAINED_GLASS_PANE));
            }
            abstimmungGUI.setItem(10, bauItem(ChatColor.GREEN + "Ja!", ChatColor.GRAY + "Stimme hiermit für " + ChatColor.GREEN + "Ja " + ChatColor.GRAY + "ab", Material.GREEN_CONCRETE));
            abstimmungGUI.setItem(16, bauItem(ChatColor.RED + "Nein!", ChatColor.GRAY + "Stimme hiermit für " + ChatColor.RED + "Nein " + ChatColor.GRAY + "ab", Material.RED_CONCRETE));
            abstimmungGUI.setItem(18, bauItem(ChatColor.GOLD + "Abstimmung", " ", Material.PAPER));
            guiListe.add(abstimmungGUI); //2


            //ENDE

            //StatusGui
            Inventory statusGui = Bukkit.createInventory(player, 27, ChatColor.GRAY + ">> " + ChatColor.GOLD + "Abstimmung");
            for (int j = 0; j < 27; j++) {
                statusGui.setItem(j, bauItem(" ", " ", Material.BLACK_STAINED_GLASS_PANE));
            }
            statusGui.setItem(11, bauItem(ChatColor.GREEN + "→ " + ChatColor.GREEN + ersteAntwort, "°" + ChatColor.YELLOW + "Stimmen: °" + ChatColor.AQUA + "{" + votesErsteAntwort + ChatColor.AQUA + "} ", Material.GREEN_STAINED_GLASS));
            statusGui.setItem(15, bauItem(ChatColor.RED + "→ " + ChatColor.RED + zweiteAntwort, "°" + ChatColor.YELLOW + "Stimmen: °" + ChatColor.AQUA + "{" + votesZweiteAntwort + ChatColor.AQUA + "} ", Material.RED_STAINED_GLASS));

            guiListe.add(statusGui); //3

            //Ende

            //ErgebnisGui


            ergebnisGui = Bukkit.createInventory(player, 27, ChatColor.GRAY + ">> " + ChatColor.BOLD + ChatColor.YELLOW + "ERGEBNIS");
            for(int s = 0; s < 27; s++)
            {
               ergebnisGui.setItem(s, bauItem(" ", " ", Material.BLACK_STAINED_GLASS_PANE));
            }

        }
    }
    public void updateErgebnisGui()
    {
          if(ersteAntwort.contains("$")) {
              ergebnisGui.setItem(10, bauItem(ChatColor.GREEN + "→ " + ChatColor.translateAlternateColorCodes('$', ersteAntwort), "°" + ChatColor.AQUA + "Stimme(n): °" + ChatColor.AQUA + votesErsteAntwort + ChatColor.YELLOW + " Stimmen für diese Antwort", Material.GREEN_CONCRETE));
          } else{
              ergebnisGui.setItem(10, bauItem(ChatColor.GREEN + "→ " + ChatColor.GOLD + ersteAntwort, "°" + ChatColor.AQUA + "Stimmen: °" + ChatColor.AQUA + votesErsteAntwort + ChatColor.YELLOW + " Stimmen für diese Antwort", Material.GREEN_CONCRETE));
          }

          if(zweiteAntwort.contains("$")) {
          ergebnisGui.setItem(16, bauItem(ChatColor.RED + "→ " + ChatColor.translateAlternateColorCodes('$', zweiteAntwort), "°" + ChatColor.AQUA + "Stimme(n): °" + ChatColor.AQUA + votesZweiteAntwort + ChatColor.YELLOW + " Stimmen für diese Antwort", Material.RED_CONCRETE));
          } else{
          ergebnisGui.setItem(16, bauItem(ChatColor.RED + "→ " + ChatColor.GOLD + zweiteAntwort, "°" + ChatColor.AQUA + "Stimmen: °" + ChatColor.AQUA + votesZweiteAntwort + ChatColor.YELLOW + " Stimmen für diese Antwort", Material.RED_CONCRETE));
          }

          ergebnisGui.setItem(22, bauItem(ChatColor.YELLOW + "Siehe hier das Ergebnis der Abstimmung ein", " ", Material.PAPER));

    }

    public void updateStatusGui() {
        if(ersteAntwort.contains("$"))
        {
            guiListe.get(3).setItem(11, bauItem(ChatColor.GREEN + "→ " + ChatColor.translateAlternateColorCodes('$', ersteAntwort) , "°" + ChatColor.YELLOW + "Stimmen: °" + ChatColor.AQUA + "{" + votesErsteAntwort + ChatColor.AQUA + "} ", Material.GREEN_STAINED_GLASS));
        }else{
            guiListe.get(3).setItem(11, bauItem(ChatColor.GREEN + "→ " + ChatColor.GOLD + ersteAntwort , "°" + ChatColor.YELLOW + "Stimmen: °" + ChatColor.AQUA + "{" + votesErsteAntwort + ChatColor.AQUA + "} ", Material.GREEN_STAINED_GLASS));
        }

        if(zweiteAntwort.contains("$"))
        {
            guiListe.get(3).setItem(15, bauItem(ChatColor.RED + "→ " + ChatColor.translateAlternateColorCodes('$', zweiteAntwort) , "°" + ChatColor.YELLOW + "Stimmen: °" + ChatColor.AQUA + "{" + votesZweiteAntwort + ChatColor.AQUA + "} ", Material.RED_STAINED_GLASS));
        }else{
            guiListe.get(3).setItem(15, bauItem(ChatColor.RED + "→ " + ChatColor.GOLD + zweiteAntwort , "°" + ChatColor.YELLOW + "Stimmen: °" + ChatColor.AQUA + "{" + votesZweiteAntwort + ChatColor.AQUA + "} ", Material.RED_STAINED_GLASS));

        }


    }

    public ItemStack bauItem(String displayName, String loreText, Material mat) //Hilfsmethode für die GUIs
    {

        ItemStack itemstack = new ItemStack(mat);
        ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        List<String> lore = new ArrayList<>();

        String[] loreString = loreText.split("°");
        for (String s : loreString) {
            lore.add(ChatColor.translateAlternateColorCodes('$', s));
        }

        meta.setLore(lore);
        itemstack.setItemMeta(meta);
        return itemstack;
    }

    public List<Inventory> getGuiListe() {
        return guiListe;

    }

    public void oeffneAntwortmoeglichkeitenGui(Player player) { //Sorgt für das Erstellen beider Antwortmöglichkeite, falls man Ja und Nein nicht will.
        nutzen = "antworten";
        stufe = 1;
        runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (stufe == 1) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Schreibe die erste Antwortmöglichkeit in den Chat!"));
                } else if (stufe == 2) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Schreibe die zweite Antwortmöglichkeit in den Chat!"));


                } else if (stufe == 3) {
                    player.sendMessage(ChatColor.GOLD + "[Poll-Builder] " + ChatColor.AQUA + "Die beiden Antwortmöglichkeiten der Umfrage sind " + ChatColor.RESET +  ChatColor.translateAlternateColorCodes('$', ersteAntwort)  + ChatColor.AQUA + " und " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('$', zweiteAntwort) );
                    ItemMeta schildmeta = guiListe.get(0).getItem(10).getItemMeta();
                    StringBuilder b = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        b.append(schildmeta.getLore().get(i)).append("°");
                    }
                    String bT = b.toString();

                    String text = bT + "\n°$eDie erste Antwortmöglichkeit ist: °" + ChatColor.GOLD + ersteAntwort + "°$eDie zweite Antwortmöglichkeit ist: °" + ChatColor.GOLD + zweiteAntwort;

                    guiListe.get(0).setItem(10, bauItem(schildmeta.getDisplayName(), text, Material.OAK_SIGN));

                    stufe = 1;


                    player.openInventory(guiListe.get(0));


                    Bukkit.getScheduler().cancelTask(runnable);
                }

            }
        }, 0L, 20L);
    }

    public void oeffneFrageGui(Player player) //Spieler kann dadurch die Frage erstellen
    {
        nutzen = "frage";
        runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (stufe == 1) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Bitte schreibe die Frage für die Umfrage in den Chat!"));

                    frageEingegeben = false;
                } else if (stufe == 2) {
                    frageEingegeben = true;
                    player.sendMessage(ChatColor.GOLD + "[Poll-Builder] " + ChatColor.AQUA + "Die Frage der Umfrage ist " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('$', frage));


                    ItemMeta schildmeta = guiListe.get(0).getItem(12).getItemMeta();
                    StringBuilder b = new StringBuilder();
                    for (int i = 0; i < 3; i++) {
                        b.append(schildmeta.getLore().get(i)).append("°");
                    }
                    String bT = b.toString();
                    String text = bT + "\n°$eDie Frage der Umfrage ist: °" + ChatColor.GOLD + frage;
                    guiListe.get(0).setItem(12, bauItem(schildmeta.getDisplayName(), text, Material.PAPER));
                    player.openInventory(guiListe.get(0));
                    stufe = 1;


                    Bukkit.getScheduler().cancelTask(runnable);
                }
            }
        }, 0L, 20L);
    }

    public void oeffneZeitGui(Player player) //Spieler kann hier die Zeit für die Umfrage einstellen
    {
        player.openInventory(guiListe.get(1));

    }

    public void setzeZugriff() {
        if (guiListe.get(0).getItem(14).getType().equals(Material.GREEN_DYE)) {
            guiListe.get(0).setItem(14, bauItem("§8» §cZugriff", "\n°" + ChatColor.YELLOW + "nur " + ChatColor.RED + "Team", Material.RED_DYE));

            ItemStack fertigStack = guiListe.get(0).getItem(31);
            ItemMeta fertigMeta = fertigStack.getItemMeta();
            StringBuilder fertigB = new StringBuilder();
            for (String zeile : fertigMeta.getLore()) {
                fertigB.append(zeile).append("°");
            }
            fertigB.append("°");
            fertigB.append(ChatColor.LIGHT_PURPLE + "[Zugriff]°");
            fertigB.append(ChatColor.YELLOW + "Nur " + ChatColor.RED + "Team °");
            fertigB.append("\n");

            guiListe.get(0).setItem(31, bauItem(fertigMeta.getDisplayName(), fertigB.toString(), Material.GREEN_WOOL));
            zugriff = "Nur" + ChatColor.RED + " Team";

        } else {
            guiListe.get(0).setItem(14, bauItem("§8» §cZugriff", "\n°" + ChatColor.YELLOW + "Alle", Material.GREEN_DYE));
            zugriff = "Alle";


        }
    }

    public void setzeZeit(int zeit) {
        int index = 0;
        int i;
        for (i = 0; i < zeiten.length; i++) {
            if (zeiten[i] == zeit) {
                index = i;
            }
        }

        this.zeit = zeiten[index];
        ItemStack zeitStack = guiListe.get(0).getItem(16);
        ItemMeta zeitMeta = zeitStack.getItemMeta();
        String lore = ChatColor.GREEN + "Dauer » " + ChatColor.AQUA + this.zeit;
        guiListe.get(0).setItem(16, bauItem(zeitMeta.getDisplayName(), lore, Material.CLOCK));


    }

    public void ueberpruefePoll() {
        startZaehler = 0;
        if (frage.isEmpty()) {
            guiListe.get(0).setItem(21, bauItem(ChatColor.RED + "Frage ❌", ChatColor.DARK_RED + "Du musst eine Frage angeben, °" + ChatColor.DARK_RED + "bevor du die Umfrage starten kannst!°" + ChatColor.RED + "Klicke dafür auf das Papier.", Material.RED_STAINED_GLASS));
        } else {
            guiListe.get(0).setItem(21, bauItem(ChatColor.GREEN + "Frage ☑", " ", Material.GREEN_STAINED_GLASS));
            startZaehler++;
        }

        if (zeit == 0) {
            guiListe.get(0).setItem(25, bauItem(ChatColor.RED + "Abstimmungszeit ❌", ChatColor.DARK_RED + "Du musst die Abstimmungszeit angeben, °" + ChatColor.DARK_RED + "bevor du die Umfrage starten kannst!°" + ChatColor.RED + "Klicke dafür auf das Papier!", Material.RED_STAINED_GLASS));
        } else {
            guiListe.get(0).setItem(25, bauItem(ChatColor.GREEN + "Zeit ☑", " ", Material.GREEN_STAINED_GLASS));
            startZaehler++;
        }
        guiListe.get(0).setItem(19, bauItem(ChatColor.GREEN + "Antwortmöglichkeiten ☑", " ", Material.GREEN_STAINED_GLASS));
        guiListe.get(0).setItem(23, bauItem(ChatColor.GREEN + "Zugriff ☑", " ", Material.GREEN_STAINED_GLASS));
        if (startZaehler == 2) {
            guiListe.get(0).setItem(31, bauItem(ChatColor.GOLD + "Bestätigung der Umfrage", ChatColor.YELLOW + "Bestätige hiermit die Veröffentlichung der Umfrage °" + ChatColor.YELLOW + "mit diesen Einstellungen: °" + "°" + ChatColor.LIGHT_PURPLE + "[Frage] °" + ChatColor.YELLOW + frage + "°°" + ChatColor.GOLD + "[Antwortmöglichkeiten] °" + ChatColor.YELLOW + ersteAntwort + "°" + ChatColor.YELLOW + zweiteAntwort + "°°" + ChatColor.RED + "[Zugriff] °" + ChatColor.YELLOW + zugriff + "°°" + ChatColor.AQUA + "[Zeit] °" + ChatColor.YELLOW + zeit + " Minuten", Material.GOLD_BLOCK));
        }
    }

    public void starteUmfrage(Player player) {
        if (zugriff.equalsIgnoreCase("alle")) {
            umfrage = new Poll(player, frage, true, zeit, ersteAntwort, zweiteAntwort, this);
        } else {
            umfrage = new Poll(player, frage, false, zeit, ersteAntwort, zweiteAntwort, this);
        }
        player.closeInventory();
    }

    public void setzeStartStack() {
        guiListe.get(0).setItem(31, bauItem("§2» §2Start", "\n°" + ChatColor.YELLOW + "Starte hiermit die Abstimmung, wenn°" + ChatColor.YELLOW + "du zufrieden mit deinen Einstellungen°" + ChatColor.YELLOW + "bist", Material.GREEN_WOOL));

    }

    public void setzeAbstimmungGuiStacks() {
        if (!ersteAntwort.equals("Ja")) {
            guiListe.get(2).setItem(10, bauItem(ChatColor.translateAlternateColorCodes('$', ersteAntwort), ChatColor.GRAY + "Stimme hiermit für diese Antwort ab", Material.GREEN_CONCRETE));
            guiListe.get(2).setItem(16, bauItem(ChatColor.translateAlternateColorCodes('$', zweiteAntwort), ChatColor.GRAY + "Stimme hiermit für diese Antwort ab", Material.RED_CONCRETE));

        }
        if (!frage.isEmpty()) {
            guiListe.get(2).setItem(18, bauItem(ChatColor.GOLD + "Abstimmung", frage, Material.PAPER));
        }

    }

    public void resetAlleWerte()
    {
        guiListe = new ArrayList<>();
        stufe = 1;
        zeit = 0;
        frage = "";
        ersteAntwort = "Ja";
        zweiteAntwort = "Nein";
        zugriff = "Alle";
        frageEingegeben = false;
        nutzen = "";

        schonGevotet = new ArrayList<>();
        esWirdPollGebaut = false;
        esLaueftGeradeEinPoll = false;
        umfrage = null;
        aktSpieler = null;
        votesErsteAntwort = 0;
        votesZweiteAntwort = 0;


        if(umfrage != null)
        {
            umfrage.stopRunnable();
        }
        try{
            Bukkit.getScheduler().cancelTask(runnable);
        }catch(Exception e)
        {

        }
    }
    public void beendeUmfrage(Player player)
    {
        if(esLaueftGeradeEinPoll)
        {
            umfrage.stoppeRunnableNormal();
            ergebnisReady = true;

            resetAlleWerte();
        }else{
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es läuft aktuell keine Umfrage, die man beenden könnte");
        }
    }

    public int getFrageStufe() {
        return stufe;
    }

    public void setFrageStufe() {
        stufe++;
    }

    public void setErsteAntwort(String ersteAntwort) {
        this.ersteAntwort = ersteAntwort;
    }

    public void setZweiteAntwort(String zweiteAntwort) {
        this.zweiteAntwort = zweiteAntwort;
    }

    public Player getAktSpieler() {
        return aktSpieler;
    }

    public String getNutzen() {
        return nutzen;
    }

    public void setFrage(String frage) {
        this.frage = frage;
    }

    public boolean getFrageEingegebn() {
        return frageEingegeben;
    }

    public void setzeInventarVonSpielerZurueck(Player player) {
        player.getInventory().setContents(zwischenSpeicher);
    }

    public void setzeZwischenSpeicher(ItemStack[] content) {
        zwischenSpeicher = content;
    }

    public void setzeNutzenZurueck() {
        nutzen = " ";
    }

    public void erhoeheVotesErsteAntwort() {
        votesErsteAntwort++;
    }

    public void erhoheVotesZweiteAntwort() {
        votesZweiteAntwort++;
    }

    public List<Player> getSchonGevotet() {
        return schonGevotet;
    }

    public void addSchonGevotet(Player player) {
        schonGevotet.add(player);
    }

    public boolean getEsWirdPollGebaut() {
        return esWirdPollGebaut;
    }

    public boolean getPollLaueft() {
        return esLaueftGeradeEinPoll;
    }

    public void setzePollLaeuftZurueck() {
        esLaueftGeradeEinPoll = false;
    }

    public void setzePollLaeuft() {
        esLaueftGeradeEinPoll = true;
        esWirdPollGebaut = false;
    }
    public void oeffneErgebnisGui(Player player)
    {
        player.openInventory(ergebnisGui);
    }
    public Inventory getErgebnisGui()
    {
        return ergebnisGui;
    }
    public String getZugriff()
    {
        return zugriff;
    }
    public boolean getErgebnisReady()
    {
        return ergebnisReady;
    }
    public void setErgebnisReady()
    {
        ergebnisReady = true;
    }


}