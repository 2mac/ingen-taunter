package base.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author 2mac
 */
public class Taunter extends JavaPlugin
{
    private ArrayList<String> taunts;
    private ArrayList<String> deathTaunts;
    private File loginFile, deathFile;
    private YamlConfiguration login, death;
    private boolean[] flags = {true,true};

    public Taunter()
    {}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        String commandText = command.getLabel().toLowerCase();
        if (commandText.equals("taunter"))
        {
            if (args[0].equalsIgnoreCase("reload"))
            {
                // todo: reload
            }
        }
        return false;
    }

    public int getRandom(int bottom, int top)
    {
        Random dice = new Random();
        int dif = top - bottom;
        return bottom + dice.nextInt(dif+1);
    }
    //Because Youtube said so...
    @Override
    public void onEnable()
    {
        login = new YamlConfiguration();
        death = new YamlConfiguration();
        taunts = new ArrayList<>();
        deathTaunts = new ArrayList<>();
        loginFile = new File(getDataFolder(), "loginmessages.yml");
        deathFile = new File(getDataFolder(), "deathmessages.yml");
        loadYamls();
        if (populateLists())
        {
            getLogger().info("Ready to taunt!");
        }

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new TauntTimeListener(this), this);
    }
    //Because Youtube said so...
    @Override
    public void onDisable() {
        
    }
    
    private void loadYamls()
    {
        try
        {
            login.load(loginFile);
            death.load(deathFile);
        } catch (IOException | InvalidConfigurationException e)
        {
            getLogger().severe("Could not load YML files");
            e.printStackTrace();
        }
    }

    private boolean populateLists()
    {
        try
        {
            for (int i = 0; i < login.getStringList("taunts").size(); i++)
            {
                taunts.add(login.getStringList("taunts").get(i));
            }

            for (int i = 0; i < death.getStringList("taunts").size(); i++)
            {
                deathTaunts.add(death.getStringList("taunts").get(i));
            }

            return true;
        } catch (Exception e)
        {
            getLogger().severe("Could not populate taunt lists!");
            e.printStackTrace();
        }

        return false;
    }

    private class TauntTimeListener implements Listener
    {
        private Taunter plugin;
        TauntTimeListener(Taunter t)
        {
            plugin = t;
        }

        @EventHandler
        public void onPlayerLogin(PlayerJoinEvent event)
        {
            if (flags[0] && taunts.size() > 0)
            {
                event.getPlayer().sendMessage(taunts.get(getRandom(0, taunts.size() - 1)));
                getLogger().info("Taunted " + event.getPlayer().getName() + " for logging in.");
            }
        }

        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent event)
        {
            if (flags[1] && deathTaunts.size() > 0)
            {
                Bukkit.broadcastMessage(deathTaunts.get(getRandom(0, deathTaunts.size() - 1)));
                getLogger().info("Taunted " + event.getEntity().getName() + " for dying.");
            }
        }
    }
}
