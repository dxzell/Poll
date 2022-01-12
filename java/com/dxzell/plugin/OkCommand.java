package com.dxzell.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OkCommand implements CommandExecutor {
    private Main main;
    public OkCommand(Main main) {
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
      if(sender instanceof Player)
      {
          Player player = (Player) sender;
          if(main.getEsWirdPollGebaut()) {

              if (player.equals(main.getAktSpieler())) {


                  if (cmd.getName().equalsIgnoreCase("ok")) {
                      if (main.getNutzen().equalsIgnoreCase("antworten")) {
                          if (main.getFrageStufe() == 1) {
                              main.setFrageStufe();
                             player.sendMessage(net.md_5.bungee.api.ChatColor.GOLD + "[Poll-Builder] " + net.md_5.bungee.api.ChatColor.AQUA + "Gib nun die zweite Antwortmöglichkeit der Umfrage in den Chat ein und bestätige diese, indem du auf " + org.bukkit.ChatColor.GREEN + "[Ja]" + org.bukkit.ChatColor.AQUA + " klickst!");
                          } else if (main.getFrageStufe() == 2) {
                              main.setFrageStufe();
                              main.setzeNutzenZurueck();
                              main.setzeAbstimmungGuiStacks();
                          }
                      } else if (main.getNutzen().equalsIgnoreCase("frage") && main.getFrageEingegebn() == false) {
                          main.setFrageStufe();
                          main.setzeNutzenZurueck();
                          main.setzeAbstimmungGuiStacks();
                      }
                  }


              }
          }else{
          player.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + "Poll" + ChatColor.GRAY + "] " + ChatColor.RED + "Es wird aktuell keine Umfrage gebaut!");
      }
      }


        return false;
    }
}
