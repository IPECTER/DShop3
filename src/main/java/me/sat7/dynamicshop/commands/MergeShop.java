package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.utilities.LangUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_MERGE_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class MergeShop extends DSCMD
{
    public MergeShop()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_MERGE_SHOP;
        validArgCount.add(3);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "mergeshop")));
        player.sendMessage(" - " + papi(player,t("HELP.USAGE") + ": /ds mergeshop <shop1> <shop2>"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (args[1].equals(args[2]))
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("ERR.WRONG_USAGE")));
            return;
        }

        if (ShopUtil.shopConfigFiles.containsKey(args[1]) && ShopUtil.shopConfigFiles.containsKey(args[2]))
        {
            ShopUtil.mergeShop(args[1], args[2]);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("MESSAGE.CHANGES_APPLIED") + args[1]));
        }
        else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("ERR.SHOP_NOT_FOUND")));
        }
    }
}
