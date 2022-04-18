package me.sat7.dynamicshop.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

import java.util.UUID;

import static me.sat7.dynamicshop.constants.Constants.*;
import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class Help
{
    private Help()
    {

    }

    // 명령어 도움말 표시
    public static void showHelp(String helpcode, Player player, String[] args)
    {
        if (!DynamicShop.ccUser.get().getBoolean(player.getUniqueId() + ".cmdHelp"))
            return;

        UUID uuid = player.getUniqueId();
        if (DynamicShop.userTempData.get(uuid).equals(helpcode))
            return;

        DynamicShop.userTempData.put(uuid, helpcode);

        if (helpcode.equals("main"))
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "main")));
            player.sendMessage(" - shop: " + papi(player,t("HELP.SHOP")));
            player.sendMessage(" - qsell: " + papi(player,t("HELP.QSELL")));
            player.sendMessage(" - cmdHelp: " + papi(player,t("HELP.CMD")));
            if (player.hasPermission(P_ADMIN_CREATE_SHOP))
                player.sendMessage("§e - createshop: " + papi(player,t("HELP.CREATE_SHOP")));
            if (player.hasPermission(P_ADMIN_DELETE_SHOP))
                player.sendMessage("§e - deleteshop: " + papi(player,t("HELP.DELETE_SHOP")));
            if (player.hasPermission(P_ADMIN_RELOAD))
                player.sendMessage("§e - reload: " + papi(player,t("HELP.RELOAD")));
            player.sendMessage("");
        } else if (helpcode.equals("shop"))
        {
            player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "shop")));

            player.sendMessage(" - " + papi(player,t("HELP.USAGE") + ": /ds shop [<shopname>]"));
            if (player.hasPermission(P_ADMIN_SHOP_EDIT) || player.hasPermission(P_ADMIN_EDIT_ALL))
            {
                player.sendMessage(" - " + papi(player,t("HELP.USAGE")
                        + ": /ds shop <shopname> <enable | addhand | add | edit | editall | setToRecAll | sellbuy | permission | maxpage | flag | position | shophours | fluctuation | stockStabilizing | account | log>"));
            }

            if (player.hasPermission(P_ADMIN_SHOP_EDIT))
            {
                player.sendMessage("§e - enable: " + papi(player,t("HELP.SHOP_ENABLE")));
                player.sendMessage("§e - addhand: " + papi(player,t("HELP.SHOP_ADD_HAND")));
                player.sendMessage("§e - add: " + papi(player,t("HELP.SHOP_ADD_ITEM")));
                player.sendMessage("§e - edit: " + papi(player,t("HELP.SHOP_EDIT")));
            }

            if (player.hasPermission(P_ADMIN_EDIT_ALL))
                player.sendMessage("§e - editall: " + papi(player,t("HELP.EDIT_ALL")));
            if (player.hasPermission(P_ADMIN_SHOP_EDIT))
                player.sendMessage("§e - setToRecAll: " + papi(player,t("HELP.SET_TO_REC_ALL")));
            player.sendMessage("");
        } else if (helpcode.equals("open_shop"))
        {
            CMDManager.openShop.SendHelpMessage(player);
        } else if (helpcode.equals("enable"))
        {
            CMDManager.enable.SendHelpMessage(player);
        } else if (helpcode.equals("add_hand") && player.hasPermission(P_ADMIN_SHOP_EDIT))
        {
            CMDManager.addHand.SendHelpMessage(player);

            ItemStack tempItem = player.getInventory().getItemInMainHand();

            if (tempItem.getType() != Material.AIR)
            {
                int idx = ShopUtil.findItemFromShop(args[1], tempItem);
                if (idx != -1)
                {
                    player.sendMessage("");
                    ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_ALREADY_EXIST");
                }
            } else
            {
                player.sendMessage(" - " + papi(player,t("ERR.HAND_EMPTY2")));
            }

            player.sendMessage("");
        } else if (helpcode.startsWith("add") && player.hasPermission(P_ADMIN_SHOP_EDIT))
        {
            if (helpcode.length() > "add".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3]));
                    int idx = ShopUtil.findItemFromShop(args[1], tempItem);

                    if (idx != -1)
                    {
                        ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_ALREADY_EXIST");
                        player.sendMessage("");
                    }
                } catch (Exception ignored)
                {
                }
            } else
            {
                CMDManager.add.SendHelpMessage(player);
            }
        } else if (helpcode.contains("edit") && !helpcode.equals("edit_all") && player.hasPermission(P_ADMIN_SHOP_EDIT))
        {
            if (helpcode.length() > "edit".length())
            {
                try
                {
                    ItemStack tempItem = new ItemStack(Material.getMaterial(args[3].substring(args[3].indexOf("/") + 1)));
                    int idx = ShopUtil.findItemFromShop(args[1], tempItem);

                    if (idx != -1)
                    {
                        ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_INFO");
                        player.sendMessage(" - " + papi(player,t("HELP.REMOVE_ITEM")));
                        player.sendMessage("");
                    }
                } catch (Exception ignored)
                {
                }
            } else
            {
                CMDManager.edit.SendHelpMessage(player);
            }
        } else if (helpcode.equals("edit_all"))
        {
            CMDManager.editAll.SendHelpMessage(player);
        } else if (helpcode.equals("set_to_rec_all"))
        {
            CMDManager.setToRecAll.SendHelpMessage(player);
        } else if (helpcode.equals("cmd_help"))
        {
            CMDManager.commandHelp.SendHelpMessage(player);
        } else if (helpcode.equals("create_shop"))
        {
            CMDManager.createShop.SendHelpMessage(player);
        } else if (helpcode.equals("delete_shop"))
        {
            CMDManager.deleteShop.SendHelpMessage(player);
        } else if (helpcode.equals("merge_shop"))
        {
            CMDManager.mergeShop.SendHelpMessage(player);
        } else if (helpcode.equals("rename_shop"))
        {
            CMDManager.renameShop.SendHelpMessage(player);
        } else if (helpcode.equals("permission"))
        {
            CMDManager.permission.SendHelpMessage(player);
        } else if (helpcode.equals("max_page"))
        {
            CMDManager.maxPage.SendHelpMessage(player);
        } else if (helpcode.equals("flag"))
        {
            CMDManager.flag.SendHelpMessage(player);
        } else if (helpcode.equals("position"))
        {
            CMDManager.position.SendHelpMessage(player);
        } else if (helpcode.equals("shophours"))
        {
            CMDManager.shopHours.SendHelpMessage(player);
        } else if (helpcode.equals("fluctuation"))
        {
            CMDManager.fluctuation.SendHelpMessage(player);
        } else if (helpcode.equals("stock_stabilizing"))
        {
            CMDManager.stockStabilizing.SendHelpMessage(player);
        } else if (helpcode.equals("account"))
        {
            CMDManager.account.SendHelpMessage(player);
        } else if (helpcode.equals("set_tax"))
        {
            CMDManager.setTax.SendHelpMessage(player);
        } else if (helpcode.equals("set_default_shop"))
        {
            CMDManager.setDefaultShop.SendHelpMessage(player);
        } else if (helpcode.equals("delete_old_user"))
        {
            CMDManager.deleteUser.SendHelpMessage(player);
        } else if (helpcode.equals("sellbuy"))
        {
            CMDManager.sellBuy.SendHelpMessage(player);
        } else if (helpcode.equals("log"))
        {
            CMDManager.log.SendHelpMessage(player);
        }
    }
}
