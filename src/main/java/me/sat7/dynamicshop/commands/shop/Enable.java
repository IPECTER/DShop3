package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class Enable extends DSCMD
{
    public Enable()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "enable")));
        player.sendMessage(" - " + papi(player,t("HELP.USAGE") + ": /ds shop <shopname> <true|false>"));
        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        if (args[3].equalsIgnoreCase("true"))
        {
            shopData.get().set("Options.enable", true);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("MESSAGE.CHANGES_APPLIED") + LangUtil.papi(sender,t("SHOP_SETTING.STATE") + ":" + args[3])));
        } else if (args[3].equalsIgnoreCase("false"))
        {
            shopData.get().set("Options.enable", false);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("MESSAGE.CHANGES_APPLIED") + LangUtil.papi(sender,t("SHOP_SETTING.STATE") + ":" + args[3])));
        } else
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("ERR.WRONG_USAGE")));
            return;
        }

        shopData.save();
    }
}
