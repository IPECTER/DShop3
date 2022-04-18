package me.sat7.dynamicshop.commands;

import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_DELETE_SHOP;
import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class DeleteShop extends DSCMD
{
    public DeleteShop()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_DELETE_SHOP;
        validArgCount.add(2);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "§c§ldeleteshop§f§r")));
        player.sendMessage(" - " + papi(player,t("HELP.USAGE") + ": /ds deleteshop <shopname>"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        if (ShopUtil.shopConfigFiles.containsKey(args[1]))
        {
            CustomConfig data = ShopUtil.shopConfigFiles.get(args[1]);
            data.delete();

            ShopUtil.shopConfigFiles.remove(args[1]);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("MESSAGE.SHOP_DELETED")));
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("ERR.SHOP_NOT_FOUND")));
        }
    }
}
