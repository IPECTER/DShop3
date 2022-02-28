package me.sat7.dynamicshop.commands.shop;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.commands.DSCMD;
import me.sat7.dynamicshop.commands.Shop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.entity.Player;

import static me.sat7.dynamicshop.constants.Constants.P_ADMIN_SHOP_EDIT;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class EditAll extends DSCMD
{
    public EditAll()
    {
        permission = P_ADMIN_SHOP_EDIT;
        validArgCount.add(6);
    }

    @Override
    public void SendHelpMessage(Player player)
    {
        player.sendMessage(DynamicShop.dsPrefix + t("HELP.TITLE").replace("{command}", "editall"));
        player.sendMessage(" - " + t("HELP.USAGE") + ": /ds shop <shopname> editall <value | median | stock | max stock> <= | + | - | * | /> <amount>");
        player.sendMessage(" - " + t("HELP.EDIT_ALL"));
        player.sendMessage(" - " + t("HELP.EDIT_ALL_2"));

        player.sendMessage("");
    }

    @Override
    public void RunCMD(String[] args, Player player)
    {
        if(!CheckValid(args, player))
            return;

        String shopName = Shop.GetShopName(args);
        CustomConfig shopData = ShopUtil.shopConfigFiles.get(shopName);

        String mod;
        float value = 0;
        String dataType;

        try
        {
            dataType = args[3];
            if (!dataType.equals("stock") && !dataType.equals("median") && !dataType.equals("value") && !dataType.equals("valueMin") && !dataType.equals("valueMax") && !dataType.equals("maxStock"))
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                return;
            }

            mod = args[4];
            if (!mod.equals("=") &&
                    !mod.equals("+") && !mod.equals("-") &&
                    !mod.equals("*") && !mod.equals("/"))
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
                return;
            }

            if (!args[5].equals("stock") && !args[5].equals("median") && !args[5].equals("value") && !args[5].equals("valueMin") && !args[5].equals("valueMax") && !args[5].equals("maxStock"))
                value = Float.parseFloat(args[5]);
        } catch (Exception e)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_DATATYPE"));
            return;
        }

        // 수정
        for (String s : shopData.get().getKeys(false))
        {
            try
            {
                @SuppressWarnings("unused") int i = Integer.parseInt(s); // 의도적으로 넣은 코드임. 숫자가 아니면 건너뛰기 위함.
                if (!shopData.get().contains(s + ".value")) continue; //장식용임
            } catch (Exception e)
            {
                continue;
            }

            switch (args[5])
            {
                case "stock":
                    value = shopData.get().getInt(s + ".stock");
                    break;
                case "median":
                    value = shopData.get().getInt(s + ".median");
                    break;
                case "value":
                    value = shopData.get().getInt(s + ".value");
                    break;
                case "valueMin":
                    value = shopData.get().getInt(s + ".valueMin");
                    break;
                case "valueMax":
                    value = shopData.get().getInt(s + ".valueMax");
                    break;
                case "maxStock":
                    value = shopData.get().getInt(s + ".maxStock");
                    break;
            }

            if (mod.equalsIgnoreCase("="))
            {
                shopData.get().set(s + "." + dataType, (int) value);
            } else if (mod.equalsIgnoreCase("+"))
            {
                shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) + value));
            } else if (mod.equalsIgnoreCase("-"))
            {
                shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) - value));
            } else if (mod.equalsIgnoreCase("/"))
            {
                if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock"))
                {
                    shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) / value));
                }
                else
                {
                    shopData.get().set(s + "." + dataType, shopData.get().getDouble(s + "." + dataType) / value);
                }
            } else if (mod.equalsIgnoreCase("*"))
            {
                if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock"))
                {
                    shopData.get().set(s + "." + dataType, (int) (shopData.get().getInt(s + "." + dataType) * value));
                }
                else
                {
                    shopData.get().set(s + "." + dataType, shopData.get().getDouble(s + "." + dataType) * value);
                }
            }

            if (shopData.get().getDouble(s + ".valueMin") < 0)
            {
                shopData.get().set(s + ".valueMin", null);
            }
            if (shopData.get().getDouble(s + ".valueMax") < 0)
            {
                shopData.get().set(s + ".valueMax", null);
            }
            if (shopData.get().getDouble(s + ".maxStock") < 1)
            {
                shopData.get().set(s + ".maxStock", null);
            }
        }
        shopData.save();
        player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.ITEM_UPDATED"));
    }
}