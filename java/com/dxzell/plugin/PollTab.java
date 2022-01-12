package com.dxzell.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        List<String> liste = new ArrayList<>();

        if(args.length == 1)
        {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(new String[] {"create", "status", "end", "delete", "ergebnis"}), new ArrayList<>());
        }
        return liste;
    }

}
