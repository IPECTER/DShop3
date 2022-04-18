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

public class MaxPage extends DSCMD
{
    public MaxPage()
    {
        inGameUseOnly = false;
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(4);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("HELP.TITLE").replace("{command}", "maxpage")));
        player.sendMessage(" - " + papi(player,t("HELP.USAGE") + ": /ds shop <shopname> maxpage <number>"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, CommandSender sender)
    {
        if(!CheckValid(args, sender))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        int newValue;
        try
        {
            newValue = Integer.parseInt(args[3]);
        } catch (Exception e)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("ERR.WRONG_DATATYPE")));
            return;
        }

        if (newValue <= 0)
        {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("ERR.VALUE_ZERO")));
        } else
        {
            shopData.get().set("Options.page", newValue);
            sender.sendMessage(DynamicShop.dsPrefix(sender) + LangUtil.papi(sender,t("MESSAGE.CHANGES_APPLIED") + args[3]));
            shopData.save();
        }
    }
}
