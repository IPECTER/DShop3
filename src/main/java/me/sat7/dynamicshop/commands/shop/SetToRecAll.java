package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.utilities.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class SetToRecAll extends DSCMD
{
    public SetToRecAll()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "SetToRecAll")));
        player.sendMessage(" - " + papi(player,t("HELP.USAGE") + ": /ds shop <shopname> SetToRecAll"));
        player.sendMessage(" - " + papi(player,t("HELP.SET_TO_REC_ALL")));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        ShopUtil.SetToRecommendedValueAll(args[1], sender);
        sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("MESSAGE.ITEM_UPDATED")));
    }
}