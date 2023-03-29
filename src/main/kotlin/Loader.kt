package CrystalDamageMain

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Loader: JavaPlugin(), Listener, CommandExecutor {

    var shilddamage = 50

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
        if (!File("CrystalDamage.properties").exists()) {
            File("CrystalDamage.properties").createNewFile()
            File("CrystalDamage.properties").writeText("CrystalDamage: ${shilddamage.toString()}")
        }
        else {
            shilddamage = (File("CrystalDamage.properties").readText().split(" ")[1]).toInt()
        }
    }

    @EventHandler
    private fun damageEvent (event: EntityDamageByEntityEvent) {
        if (event.entity is Player && (event.entity as Player).isBlocking && event.damager.type == EntityType.ENDER_CRYSTAL) {
            var p = event.entity as Player
            if (p.inventory.itemInMainHand.type == Material.SHIELD) {
                var newdurability = (p.inventory.itemInMainHand.durability) + (Material.SHIELD.maxDurability*(shilddamage/100.0))
                p.inventory.itemInMainHand.durability = newdurability.toShort()
                if (p.inventory.itemInMainHand.durability >= Material.SHIELD.maxDurability) {
                    p.inventory.itemInMainHand.type = Material.AIR
                }
            }
            else if (p.inventory.itemInOffHand.type == Material.SHIELD) {
                var newdurability = (p.inventory.itemInMainHand.durability) + (Material.SHIELD.maxDurability*(shilddamage/100.0))
                p.inventory.itemInOffHand.durability = newdurability.toShort()
                if (p.inventory.itemInOffHand.durability <= 0) {
                    p.inventory.itemInOffHand.type = Material.AIR
                }
            }
            event.damage = 0.0
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player || sender is ConsoleCommandSender) {
            when (command.name) {
                "crystaldamage" -> {
                    if (sender.isOp) {
                        if (args.size == 1 && args[0] == "query") {
                            sender.sendMessage("The Damage from Endcrystals to Shields is currently $shilddamage%")
                        } else if (args.size == 2 && args[0] == "set" && 0 <= args[1].toInt() && args[1].toInt() <= 100) {
                            shilddamage = args[1].toInt()
                            File("CrystalDamage.properties").writeText("CrystalDamage: ${shilddamage.toString()}")
                            sender.sendMessage("The Damage from Endcrystals to Shields ist now set to $shilddamage%")
                        }
                        else {
                            sender.sendMessage("§r/crystaldamage query §cto view the current value or§r\n/crystaldamage set [0-100] §cto set the value§r")
                        }
                    }
                    else {
                        sender.sendMessage("§cYou don't have the permission to do this!§r")
                    }
                }
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (sender is Player) {
            when (command.name) {
                "crystaldamage" -> {
                    if (args.size == 1) {
                        return arrayListOf<String>("set", "query")
                    }
                    else if (args.size == 2) {
                        return (-1 until 100).map { (it + 1 ).toString()}
                    }
                    else {
                        return arrayListOf<String>()
                    }
                }
            }
        }
        return null
    }


}