package com.dxzell.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PollCommands implements CommandExecutor {

    private Main main;

    public PollCommands(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("poll") && args.length == 1) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (main.getEsWirdPollGebaut() == true) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] >> " + ChatColor.RED + "Es wird bereits ein Poll gebaut!");
                    } else if (main.getPollLaueft() == true) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] >> " + ChatColor.RED + "Es läuft bereits eine Abstimmung, warte bite!");

                    } else {
                        if(player.hasPermission("citybuild.poll")) {
                            main.baueCreateGui(player);
                        }else{
                            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" +ChatColor.GRAY + "] " + ChatColor.RED + "Dir fehlen die nötigen Rechte, um eine Umfrage starten zu können");
                        }
                    }


                } else if (args[0].equalsIgnoreCase("end")) {
                    if(player.hasPermission("citybuild.poll"))
                    {
                       main.beendeUmfrage(player);
                    }else{
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Dir fehlen die nötigen Rechte, um eine Umfrage stoppen zu können");
                    }


                } else if (args[0].equalsIgnoreCase("status")) {
                    if(main.getPollLaueft()) {
                        if (main.getZugriff().equalsIgnoreCase("alle")) {
                            player.openInventory(main.getGuiListe().get(3));
                        } else {
                            if (player.hasPermission("citybuild.poll")) {
                                player.openInventory(main.getGuiListe().get(3));

                            } else {
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Dir fehlen die nötigen Rechte, um eine Umfrage starten zu können");
                            }

                        }
                    }else{
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es läuft gerade keine Umfrage!");
                    }

                    } else if (args[0].equalsIgnoreCase("delete")) {
                    if(player.hasPermission("citybuild.poll")) {
                        if(main.getPollLaueft() || main.getEsWirdPollGebaut()) {
                            if(player.equals(main.getAktSpieler())) {
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Die Abstimmung wurde erfolgreich gelöscht!");
                                player.closeInventory();
                            }else{
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Nur der Poll-Ersteller kann seine eigene Umfrage löschen!");



                            }
                        }else{
                            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es läuft aktuell keine Abstimmung und es wird auch keine gebaut!");
                        }
                    }else{
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Dir fehlen die nötigen Rechte, um eine Umfrage löschen zu können");
                    }


                }else if(args[0].equalsIgnoreCase("ergebnis"))
                {
                    if(main.getZugriff().equalsIgnoreCase("alle")) {

                        if(main.getErgebnisReady()) {

                            main.oeffneErgebnisGui(player);
                        }else {
                            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es gibt aktuell kein Ergebnis!");
                        }

                    }else{
                        if(!player.hasPermission("citybuild.poll"))
                        {
                            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Dir fehlen die nötigen Rechte, um das Ergebnis der Umfrage einzusehen!");
                        }else{
                            if(main.getErgebnisReady()) {

                                main.oeffneErgebnisGui(player);
                            }else {
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es gibt aktuell kein Ergebnis!");
                            }


                        }
                    }

                } else {
                    player.sendMessage(ChatColor.GOLD + "[Poll-Builder] " + ChatColor.RED + "Falsche Nutzung des Poll-Befehls! Die zulässigen Poll-Befehle lauten: /poll create, /poll delete, /poll end, /poll status");
                }
            } else if (cmd.getName().equalsIgnoreCase("poll") && args.length == 0) {
                if(main.getPollLaueft()) {
                    if (main.getZugriff().equalsIgnoreCase("alle")) {
                        player.openInventory(main.getGuiListe().get(2));
                    } else {
                        if (player.hasPermission("citybuild.poll")) {
                            player.openInventory(main.getGuiListe().get(2));
                        } else {
                            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Dir fehlen die nötigen Rechte, um an der Umfrage teilzunehmen!");
                        }
                    }
                }else{
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es läuft gerade keine Umfrage!");
                }

            } else if (cmd.getName().equalsIgnoreCase("poll") && args.length > 1) {


                player.sendMessage(ChatColor.GOLD + "[Poll-Builder] " + ChatColor.RED + "Falsche Nutzung des Poll-Befehls! Die zulässigen Poll-Befehle lauten: /poll create, /poll delete, /poll end, /poll status");
            }



        }

        return false;
    }
}
