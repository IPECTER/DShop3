package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.files.CustomConfig;
import me.sat7.dynamicshop.jobshook.JobsHook;
import me.sat7.dynamicshop.utilities.ConfigUtil;
import me.sat7.dynamicshop.utilities.ItemsUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ShopSettings extends InGameUI
{
    public ShopSettings()
    {
        uiType = UI_TYPE.ShopSettings;
    }

    private final int ENABLE_TOGGLE = 0;
    private final int PERMISSION = 1;
    private final int MAX_PAGE = 2;
    private final int SHOP_HOUR = 6;
    private final int SHOP_HOUR_OPEN = 7;
    private final int SHOP_HOUR_CLOSE = 8;
    private final int FLUC = 15;
    private final int FLUC_INTERVAL = 16;
    private final int FLUC_STRENGTH = 17;
    private final int STABLE = 24;
    private final int STABLE_INTERVAL = 25;
    private final int STABLE_STRENGTH = 26;

    private final int FLAG1 = 9;
    private final int FLAG2 = 10;
    private final int FLAG3 = 11;
    private final int FLAG4 = 12;
    private final int FLAG5 = 18;
    private final int FLAG6 = 19;
    private final int FLAG7 = 20;
    private final int FLAG8 = 21;
    private final int FLAG9 = 22;
    private final int FLAG10 = 13;
    private final int FLAG11 = 27;

    private final int TAX_TOGGLE = 33;
    private final int TAX_AMOUNT = 34;
    private final int LOG_TOGGLE = 42;
    private final int LOG_DELETE = 43;

    private final int CLOSE = 36;

    private String shopName;

    public Inventory getGui(Player player, String shopName)
    {
        this.shopName = shopName;

        inventory = Bukkit.createInventory(player, 45, papi(player,t("SHOP_SETTING_TITLE") + "§7 | §8" + shopName));

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);
        ConfigurationSection confSec_Options = data.get().getConfigurationSection("Options");

        // 닫기 버튼
        CreateCloseButton(player, CLOSE);

        // 활성화 버튼
        boolean isShopEnable = confSec_Options.getBoolean("enable", true);
        Material enableToggleMat = isShopEnable ? Material.GREEN_STAINED_GLASS : Material.RED_STAINED_GLASS;
        String current = isShopEnable ? papi(player,t("SHOP_SETTING.STATE_ENABLE")) : papi(player,t("SHOP_SETTING.STATE_DISABLE"));
        String set = isShopEnable ? papi(player,t("SHOP_SETTING.STATE_DISABLE")) : papi(player,t("SHOP_SETTING.STATE_ENABLE"));
        String enableToggleLore = "§9" + papi(player,t("CUR_STATE") + ": " + current);
        enableToggleLore += "\n§e" + papi(player,t("CLICK") + ": " + set);
        CreateButton(ENABLE_TOGGLE, enableToggleMat, papi(player,t("SHOP_SETTING.STATE")), enableToggleLore);

        // 권한 버튼
        String permStr = confSec_Options.getString("permission");
        String permNew = "dshop.user.shop." + shopName;
        Material permIcon;
        if (permStr == null || permStr.isEmpty())
        {
            permStr = papi(player,t("NULL(OPEN)"));
            permIcon = Material.IRON_BLOCK;
        } else
        {
            permNew = papi(player,t("NULL(OPEN)"));
            permIcon = Material.GOLD_BLOCK;
        }

        ArrayList<String> permLore = new ArrayList<>();
        permLore.add("§9" + papi(player,t("CUR_STATE") + ": " + permStr));
        if (!permStr.equalsIgnoreCase(papi(player,t("NULL(OPEN)"))))
        {
            permLore.add("§9 - " + permStr + ".buy");
            permLore.add("§9 - " + permStr + ".sell");
        }
        permLore.add("§e" + papi(player,t("CLICK") + ": " + permNew));
        CreateButton(PERMISSION, permIcon, papi(player,t("SHOP_SETTING.PERMISSION")), permLore);

        //최대 페이지 버튼
        CreateButton(MAX_PAGE, InGameUI.GetPageButtonIconMat(), papi(player,t("SHOP_SETTING.MAX_PAGE")), new ArrayList<>(Arrays.asList(papi(player,t("SHOP_SETTING.MAX_PAGE_LORE")), papi(player,t("SHOP_SETTING.L_R_SHIFT")))), data.get().getInt("Options.page"));

        // 영업시간 버튼
        int curTime = (int) (player.getWorld().getTime()) / 1000 + 6;
        if (curTime > 24) curTime -= 24;
        if (data.get().contains("Options.shophours"))
        {
            String[] temp = data.get().getString("Options.shophours").split("~");
            int open = Integer.parseInt(temp[0]);
            int close = Integer.parseInt(temp[1]);

            ArrayList<String> shopHourLore = new ArrayList<>(Arrays.asList(
                    papi(player,t("TIME.CUR").replace("{time}", curTime + "")),
                    "§9" + papi(player,t("CUR_STATE")) + ": ",
                    "§9 - " + papi(player,t("TIME.OPEN")) + ": " + open,
                    "§9 - " + papi(player,t("TIME.CLOSE")) + ": " + close,
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("TIME.OPEN24"))));
            CreateButton(SHOP_HOUR, Material.CLOCK, papi(player,t("TIME.SHOPHOURS")), shopHourLore);
            CreateButton(SHOP_HOUR_OPEN, Material.CLOCK, "§f" + papi(player,t("TIME.OPEN")), new ArrayList<>(Arrays.asList(papi(player,t("TIME.OPEN_LORE")), papi(player,t("SHOP_SETTING.L_R_SHIFT")))), open);
            CreateButton(SHOP_HOUR_CLOSE, Material.CLOCK, "§f" + papi(player,t("TIME.CLOSE")), new ArrayList<>(Arrays.asList(papi(player,t("TIME.CLOSE_LORE")), papi(player,t("SHOP_SETTING.L_R_SHIFT")))), close);
        } else
        {
            ArrayList<String> shopHourLore = new ArrayList<>(Arrays.asList(
                    papi(player,t("TIME.CUR").replace("{time}", curTime + "")),
                    "§9" + papi(player,t("CUR_STATE")) + ": " + papi(player,t("TIME.OPEN24")),
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("TIME.SET_SHOPHOURS"))));
            CreateButton(SHOP_HOUR, Material.CLOCK, papi(player,t("TIME.SHOPHOURS")), shopHourLore);
        }

        // 랜덤스톡 버튼
        ConfigurationSection flucConf = data.get().getConfigurationSection("Options.fluctuation");
        if (flucConf != null)
        {
            ArrayList<String> fluctuationLore = new ArrayList<>(Arrays.asList(
                    "§9" + papi(player,t("CUR_STATE")) + ": " + papi(player,t("ON")),
                    "§e" + papi(player,t("LMB")) + ": " + papi(player,t("OFF"))
                    ));
            CreateButton(FLUC, Material.COMPARATOR, papi(player,t("FLUCTUATION.FLUCTUATION")), fluctuationLore);

            ArrayList<String> fluctuation_interval_lore = new ArrayList<>(Arrays.asList(
                    papi(player,t("FLUCTUATION.INTERVAL_LORE")),
                    "§9" + papi(player,t("CUR_STATE")) + ": " + flucConf.getInt("interval") / 2.0 + "h",
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("SHOP_SETTING.L_R_SHIFT"))));
            CreateButton(FLUC_INTERVAL, Material.COMPARATOR, papi(player,t("FLUCTUATION.INTERVAL")), fluctuation_interval_lore, Clamp(flucConf.getInt("interval") / 2, 1, 64));

            ArrayList<String> fluctuation_strength_lore = new ArrayList<>(Arrays.asList(
                    papi(player,t("FLUCTUATION.STRENGTH_LORE")),
                    "§9" + papi(player,t("CUR_STATE")) + ": ~" + flucConf.get("strength") + "%",
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("STOCK_STABILIZING.L_R_SHIFT"))));
            CreateButton(FLUC_STRENGTH, Material.COMPARATOR, papi(player,t("FLUCTUATION.STRENGTH")), fluctuation_strength_lore, Clamp((int) (flucConf.getDouble("strength") * 10), 1, 64));
        } else
        {
            ItemStack flucToggleBtn = ItemsUtil.createItemStack(Material.COMPARATOR, null,
                    papi(player,t("FLUCTUATION.FLUCTUATION")),
                    new ArrayList<>(Arrays.asList(
                            "§9" + papi(player,t("CUR_STATE")) + ": " + papi(player,t("OFF")),
                            "§e" + papi(player,t("LMB")) + ": " + papi(player,t("ON"))
                            )),
                    1);
            inventory.setItem(FLUC, flucToggleBtn);
        }

        // 재고 안정화 버튼
        ConfigurationSection stockStableConf = data.get().getConfigurationSection("Options.stockStabilizing");
        if (stockStableConf != null)
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + papi(player,t("CUR_STATE")) + ": " + papi(player,t("ON")),
                    "§e" + papi(player,t("LMB")) + ": " + papi(player,t("OFF"))
            ));
            CreateButton(STABLE, Material.COMPARATOR, papi(player,t("STOCK_STABILIZING.SS")), stableLore);

            ArrayList<String> stable_interval_Lore = new ArrayList<>(Arrays.asList(
                    papi(player,t("FLUCTUATION.INTERVAL_LORE")),
                    "§9" + papi(player,t("CUR_STATE")) + ": " + stockStableConf.getInt("interval") / 2.0 + "h",
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("SHOP_SETTING.L_R_SHIFT"))));
            CreateButton(STABLE_INTERVAL, Material.COMPARATOR, papi(player,t("FLUCTUATION.INTERVAL")), stable_interval_Lore, Clamp(stockStableConf.getInt("interval") / 2, 1, 64));

            ArrayList<String> stable_strength_Lore = new ArrayList<>(Arrays.asList(
                    DynamicShop.plugin.getConfig().getBoolean("Shop.UseLegacyStockStabilization") ? papi(player,t("STOCK_STABILIZING.STRENGTH_LORE_A")) : papi(player,t("STOCK_STABILIZING.STRENGTH_LORE_B")),
                    "§9" + papi(player,t("CUR_STATE")) + ": ~" + stockStableConf.get("strength") + "%",
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("STOCK_STABILIZING.L_R_SHIFT"))));
            CreateButton(STABLE_STRENGTH, Material.COMPARATOR, papi(player,t("FLUCTUATION.STRENGTH")), stable_strength_Lore, Clamp((int) (stockStableConf.getDouble("strength") * 10), 1, 64));
        } else
        {
            ArrayList<String> stableLore = new ArrayList<>(Arrays.asList(
                    "§9" + papi(player,t("CUR_STATE")) + ": " + papi(player,t("OFF")),
                    "§e" + papi(player,t("LMB")) + ": " + papi(player,t("ON"))
                    ));
            CreateButton(STABLE, Material.COMPARATOR, papi(player,t("STOCK_STABILIZING.SS")), stableLore);
        }

        // 세금
        int globalTax = ConfigUtil.getCurrentTax();
        if (data.get().contains("Options.SalesTax"))
        {
            ArrayList<String> taxLore = new ArrayList<>(Arrays.asList(
                    "§9" + papi(player,t("CUR_STATE")) + ": " + papi(player,t("TAX.USE_LOCAL")),
                    "§e" + papi(player,t("CLICK")) + ": " +
                            papi(player,t("TAX.USE_GLOBAL").replace("{tax}", globalTax + ""))));
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, papi(player,t("TAX.SALES_TAX")), taxLore);

            ArrayList<String> taxLore2 = new ArrayList<>(Arrays.asList(
                    "§9" + papi(player,t("CUR_STATE")) + ": " + data.get().getInt("Options.SalesTax") + "%",
                    papi(player,t("SHOP_SETTING.L_R_SHIFT"))));
            CreateButton(TAX_AMOUNT, Material.IRON_INGOT, papi(player,t("TAX.SALES_TAX")), taxLore2, Clamp(data.get().getInt("Options.SalesTax"), 1, 64));
        } else
        {
            ArrayList<String> taxLore = new ArrayList<>(Arrays.asList(
                    "§9" + papi(player,t("CUR_STATE")) + ": " +
                            papi(player,t("TAX.USE_GLOBAL").replace("{tax}", globalTax + "")),
                    "§e" + papi(player,t("CLICK")) + ": " + papi(player,t("TAX.USE_LOCAL"))));
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, papi(player,t("TAX.SALES_TAX")), taxLore);
        }

        // 플래그 버튼들
        CreateFlagButton(FLAG1, confSec_Options.contains("flag.signshop"), "signShop", papi(player,t("SHOP_SETTING.SIGN_SHOP_LORE")));
        CreateFlagButton(FLAG2, confSec_Options.contains("flag.localshop"), "localShop", papi(player,t("SHOP_SETTING.LOCAL_SHOP_LORE")));
        CreateFlagButton(FLAG3, confSec_Options.contains("flag.deliverycharge"), "deliveryCharge", papi(player,t("SHOP_SETTING.DELIVERY_CHARGE_LORE")));
        CreateFlagButton(FLAG4, confSec_Options.contains("flag.jobpoint"), "jobPoint", papi(player,t("SHOP_SETTING.JOB_POINT_LORE")));
        CreateFlagButton(FLAG5, confSec_Options.contains("flag.showvaluechange"), "showValueChange", papi(player,t("SHOP_SETTING.SHOW_VALUE_CHANGE_LORE")));
        CreateFlagButton(FLAG6, confSec_Options.contains("flag.hidestock"), "hideStock", papi(player,t("SHOP_SETTING.HIDE_STOCK")));
        CreateFlagButton(FLAG7, confSec_Options.contains("flag.hidepricingtype"), "hidePricingType", papi(player,t("SHOP_SETTING.HIDE_PRICING_TYPE")));
        CreateFlagButton(FLAG8, confSec_Options.contains("flag.hideshopbalance"), "hideShopBalance", papi(player,t("SHOP_SETTING.HIDE_SHOP_BALANCE")));
        CreateFlagButton(FLAG9, confSec_Options.contains("flag.showmaxstock"), "showMaxStock", papi(player,t("SHOP_SETTING.SHOW_MAX_STOCK")));
        CreateFlagButton(FLAG10, confSec_Options.contains("flag.hiddenincommand"), "hiddenInCommand", papi(player,t("SHOP_SETTING.HIDDEN_IN_COMMAND")));
        CreateFlagButton(FLAG11, confSec_Options.contains("flag.integeronly"), "integerOnly", papi(player,t("SHOP_SETTING.INTEGER_ONLY")));

        // 로그 버튼
        String log_cur;
        String log_set;
        if (confSec_Options.contains("log"))
        {
            log_cur = papi(player,t("ON"));
            log_set = papi(player,t("OFF"));
        } else
        {
            log_cur = papi(player,t("OFF"));
            log_set = papi(player,t("ON"));
        }
        ArrayList<String> logLore = new ArrayList<>();
        logLore.add("§9" + papi(player,t("CUR_STATE") + ": " + log_cur));
        logLore.add("§e" + papi(player,t("LMB") + ": " + log_set));
        CreateButton(LOG_TOGGLE, Material.BOOK, papi(player,t("LOG.LOG")), logLore);
        CreateButton(LOG_DELETE, Material.RED_STAINED_GLASS_PANE, papi(player,t("LOG.DELETE")), "");
        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        Player player = (Player) e.getWhoClicked();

        CustomConfig data = ShopUtil.shopConfigFiles.get(shopName);

        // 닫기버튼
        if (e.getSlot() == CLOSE)
        {
            DynaShopAPI.openShopGui(player, shopName, 1);
        }
        // 활성화
        if (e.getSlot() == ENABLE_TOGGLE)
        {
            if (data.get().getBoolean("Options.enable", true))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " enable false");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " enable true");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 권한
        else if (e.getSlot() == PERMISSION)
        {
            if (data.get().getString("Options.permission").isEmpty())
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " permission true");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " permission false");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 최대 페이지
        else if (e.getSlot() == MAX_PAGE)
        {
            int oldvalue = data.get().getInt("Options.page");
            int targetValue;

            if (e.isRightClick())
            {
                targetValue = oldvalue + 1;
                if (e.isShiftClick()) targetValue += 4;
                if (targetValue >= 20) targetValue = 20;
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " maxpage " + targetValue);
            } else
            {
                targetValue = oldvalue - 1;
                if (e.isShiftClick()) targetValue -= 4;
                if (targetValue <= 1) targetValue = 1;
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " maxpage " + targetValue);
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // 영업시간
        else if (e.getSlot() == SHOP_HOUR || e.getSlot() == SHOP_HOUR_OPEN || e.getSlot() == SHOP_HOUR_CLOSE)
        {
            if (data.get().contains("Options.shophours"))
            {
                String[] shopHour = data.get().getString("Options.shophours").split("~");
                Integer open = Integer.parseInt(shopHour[0]);
                int close = Integer.parseInt(shopHour[1]);
                int edit = -1;
                if (e.isRightClick()) edit = 1;
                if (e.isShiftClick()) edit *= 5;

                if (e.getSlot() == SHOP_HOUR)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours 0 0");
                } else if (e.getSlot() == SHOP_HOUR_OPEN)
                {
                    open += edit;

                    if (open.equals(close))
                    {
                        if (e.isRightClick())
                        {
                            open += 1;
                        } else
                        {
                            open -= 1;
                        }
                    }

                    open = Clamp(open, 1, 24);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours " + open + " " + close);
                } else if (e.getSlot() == SHOP_HOUR_CLOSE)
                {
                    close += edit;

                    if (open.equals(close))
                    {
                        if (e.isRightClick())
                        {
                            close += 1;
                        } else
                        {
                            close -= 1;
                        }
                    }

                    close = Clamp(close, 1, 24);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours " + open + " " + close);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == SHOP_HOUR)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " shophours 20 6");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 랜덤스톡
        else if (e.getSlot() == FLUC || e.getSlot() == FLUC_INTERVAL || e.getSlot() == FLUC_STRENGTH)
        {
            if (data.get().contains("Options.fluctuation"))
            {
                int interval = data.get().getInt("Options.fluctuation.interval");
                double strength = data.get().getDouble("Options.fluctuation.strength");

                if (e.getSlot() == FLUC)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation off");
                } else if (e.getSlot() == FLUC_INTERVAL)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    interval += edit;
                    interval = Clamp(interval, 1, 999);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation " + interval + " " + strength);
                } else if (e.getSlot() == FLUC_STRENGTH)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    strength += edit;
                    strength = Clamp(strength, 0.1, 64);
                    strength = Math.round(strength * 100) / 100.0;

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation " + interval + " " + strength);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == FLUC)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " fluctuation 48 0.1");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 스톡 안정화
        else if (e.getSlot() == STABLE || e.getSlot() == STABLE_INTERVAL || e.getSlot() == STABLE_STRENGTH)
        {
            if (data.get().contains("Options.stockStabilizing"))
            {
                int interval = data.get().getInt("Options.stockStabilizing.interval");
                double strength = data.get().getDouble("Options.stockStabilizing.strength");

                if (e.getSlot() == STABLE)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing off");
                } else if (e.getSlot() == STABLE_INTERVAL)
                {
                    int edit = -1;
                    if (e.isRightClick()) edit = 1;
                    if (e.isShiftClick()) edit *= 5;

                    interval += edit;
                    interval = Clamp(interval, 1, 999);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing " + interval + " " + strength);
                } else if (e.getSlot() == STABLE_STRENGTH)
                {
                    double edit = -0.1;
                    if (e.isRightClick()) edit = 0.1;
                    if (e.isShiftClick()) edit *= 5;

                    strength += edit;
                    strength = Clamp(strength, 0.1, 25);
                    strength = (Math.round(strength * 100) / 100.0);

                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing " + interval + " " + strength);
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            } else
            {
                if (e.getSlot() == STABLE)
                {
                    Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " stockStabilizing 48 0.5");
                    DynaShopAPI.openShopSettingGui(player, shopName);
                }
            }
        }
        // 세금
        else if (e.getSlot() == TAX_TOGGLE || e.getSlot() == TAX_AMOUNT)
        {
            // 전역,지역 토글
            if (e.getSlot() == TAX_TOGGLE)
            {
                if (data.get().contains("Options.SalesTax"))
                {
                    data.get().set("Options.SalesTax", null);
                } else
                {
                    data.get().set("Options.SalesTax", DynamicShop.plugin.getConfig().getInt("Shop.SalesTax"));
                }

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            // 수치설정
            else if (data.get().contains("Options.SalesTax"))
            {
                int edit = -1;
                if (e.isRightClick()) edit = 1;
                if (e.isShiftClick()) edit *= 5;

                int result = Clamp(data.get().getInt("Options.SalesTax") + edit, 0, 99);
                data.get().set("Options.SalesTax", result);

                DynaShopAPI.openShopSettingGui(player, shopName);
            }
            data.save();
        }
        // signshop
        else if (e.getSlot() == FLAG1)
        {
            if (data.get().contains("Options.flag.signshop"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag signShop unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag signShop set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // localshop
        else if (e.getSlot() == FLAG2)
        {
            if (data.get().contains("Options.flag.localshop"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag localShop unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag localShop set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // deliverycharge
        else if (e.getSlot() == FLAG3)
        {
            if (data.get().contains("Options.flag.deliverycharge"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag deliveryCharge unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag deliveryCharge set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // jobpoint
        else if (e.getSlot() == FLAG4)
        {
            if (data.get().contains("Options.flag.jobpoint"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag jobPoint unset");
            } else
            {
                if (!JobsHook.jobsRebornActive)
                {
                    player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("ERR.JOBS_REBORN_NOT_FOUND")));
                    return;
                }

                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag jobPoint set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // showValueChange
        else if (e.getSlot() == FLAG5)
        {
            if (data.get().contains("Options.flag.showvaluechange"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showValueChange unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showValueChange set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDE_STOCK
        else if (e.getSlot() == FLAG6)
        {
            if (data.get().contains("Options.flag.hidestock"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideStock unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideStock set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDE_PRICING_TYPE
        else if (e.getSlot() == FLAG7)
        {
            if (data.get().contains("Options.flag.hidepricingtype"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hidePricingType unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hidePricingType set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDE_SHOP_BALANCE
        else if (e.getSlot() == FLAG8)
        {
            if (data.get().contains("Options.flag.hideshopbalance"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideShopBalance unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hideShopBalance set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // SHOW_MAX_STOCK
        else if (e.getSlot() == FLAG9)
        {
            if (data.get().contains("Options.flag.showmaxstock"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showMaxStock unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag showMaxStock set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // HIDDEN_IN_COMMAND
        else if (e.getSlot() == FLAG10)
        {
            if (data.get().contains("Options.flag.hiddenincommand"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hiddenInCommand unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag hiddenInCommand set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // INTEGER_ONLY
        else if (e.getSlot() == FLAG11)
        {
            if (data.get().contains("Options.flag.integeronly"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag integerOnly unset");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " flag integerOnly set");
            }
            DynaShopAPI.openShopSettingGui(player, shopName);
        }
        // log
        else if (e.getSlot() == LOG_TOGGLE)
        {
            if (data.get().contains("Options.log") && data.get().getBoolean("Options.log"))
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log disable");
            } else
            {
                Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log enable");
            }

            DynaShopAPI.openShopSettingGui(player, shopName);
        } else if (e.getSlot() == LOG_DELETE)
        {
            Bukkit.dispatchCommand(player, "DynamicShop shop " + shopName + " log clear");

            DynaShopAPI.openShopSettingGui(player, shopName);
        }
    }

    private void CreateFlagButton(int buttonPosition, boolean isEnable, String title, String lore)
    {
        String current;
        String set;
        Material icon;
        if (isEnable)
        {
            icon = Material.GREEN_STAINED_GLASS_PANE;
            current = papi(null,t("SET"));
            set = papi(null,t("UNSET"));
        } else
        {
            icon = Material.BLACK_STAINED_GLASS_PANE;
            current = papi(null,t("UNSET"));
            set = papi(null,t("SET"));
        }

        ArrayList<String> loreArray = new ArrayList<>();
        if(lore.contains("\n"))
        {
            String[] temp = lore.split("\n");
            Collections.addAll(loreArray, temp);
        }
        else
        {
            loreArray.add(lore);
        }
        loreArray.add("§9" + papi(null,t("CUR_STATE") + ": " + current));
        loreArray.add("§e" + papi(null,t("CLICK") + ": " + set));
        CreateButton(buttonPosition, icon, papi(null,t("SHOP_SETTING.FLAG")) + ": " + title, loreArray);
    }
}
