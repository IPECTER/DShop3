package me.sat7.dynamicshop.guis;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.events.OnChat;
import me.sat7.dynamicshop.utilities.ShopUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static me.sat7.dynamicshop.utilities.LangUtil.papi;
import static me.sat7.dynamicshop.utilities.LangUtil.t;
import static me.sat7.dynamicshop.utilities.MathUtil.Clamp;

public final class ItemPalette extends InGameUI
{
    public ItemPalette()
    {
        uiType = UI_TYPE.ItemPalette;
    }

    private final int CLOSE = 45;
    private final int PAGE = 49;
    private final int ADD_ALL = 51;
    private final int SEARCH = 53;

    private static ArrayList<ItemStack> sortedList = new ArrayList<>();
    private ArrayList<ItemStack> paletteList = new ArrayList<>();

    private Player player;
    private String shopName = "";
    private int shopSlotIndex = 0;
    private String search = "";
    private int maxPage;
    private int currentPage;

    public Inventory getGui(Player player, String shopName, int targetSlot, int page, String search)
    {
        this.player = player;
        this.shopName = shopName;
        this.shopSlotIndex = targetSlot;
        this.search = search;

        inventory = Bukkit.createInventory(player, 54, papi(player,t("PALETTE_TITLE") + "§7 | §8" + shopName));
        paletteList.clear();
        paletteList = CreatePaletteList();
        maxPage = paletteList.size() / 45 + 1;
        currentPage = Clamp(page, 1, maxPage);

        // Items
        ShowItems();

        // Close Button
        CreateCloseButton(player, CLOSE);

        // Page Button
        String pageString = papi(player,t("PALETTE.PAGE_TITLE")
                .replace("{curPage}", Integer.toString(page))
                .replace("{maxPage}", Integer.toString(maxPage)));
        CreateButton(PAGE, InGameUI.GetPageButtonIconMat(), pageString, papi(player,t("PALETTE.PAGE_LORE")), page);

        // Add all Button
        if(!paletteList.isEmpty())
            CreateButton(ADD_ALL, Material.YELLOW_STAINED_GLASS_PANE, papi(player,t("PALETTE.ADD_ALL")), "");

        // Search Button
        String filterString = search.isEmpty() ? "" : papi(player,t("PALETTE.FILTER_APPLIED") + search);
        filterString += "\n" + papi(player,t("PALETTE.FILTER_LORE"));
        CreateButton(SEARCH, Material.COMPASS, papi(player,t("PALETTE.SEARCH")), filterString);

        return inventory;
    }

    @Override
    public void OnClickUpperInventory(InventoryClickEvent e)
    {
        this.player = (Player) e.getWhoClicked();

        if (e.getSlot() == CLOSE) CloseUI();
        else if (e.getSlot() == PAGE) MovePage(e.isLeftClick(), e.isRightClick());
        else if (e.getSlot() == ADD_ALL) AddAll();
        else if (e.getSlot() == SEARCH) OnClickSearch(e.isLeftClick(), e.isRightClick());
        else if (e.getSlot() <= 45) OnClickItem(e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getCurrentItem());
    }

    @Override
    public void OnClickLowerInventory(InventoryClickEvent e)
    {
        this.player = (Player) e.getWhoClicked();

        OnClickUserItem(e.isLeftClick(), e.isRightClick(), e.getCurrentItem());
    }

    private ArrayList<ItemStack> CreatePaletteList()
    {
        ArrayList<ItemStack> paletteList = new ArrayList<>();

        if (search.length() > 0)
        {
            Material[] allMat = Material.values();
            for (Material m : allMat)
            {
                String target = m.name().toUpperCase();
                String[] temp = search.split(" ");

                if (temp.length == 1)
                {
                    if (target.contains(search.toUpperCase()))
                    {
                        paletteList.add(new ItemStack(m));
                    } else if (target.contains(search.toUpperCase().replace(" ", "_")))
                    {
                        paletteList.add(new ItemStack(m));
                    }
                } else
                {
                    String[] targetTemp = target.split("_");

                    if (targetTemp.length > 1 && temp.length > 1 && targetTemp.length == temp.length)
                    {
                        boolean match = true;
                        for (int i = 0; i < targetTemp.length; i++)
                        {
                            if (!targetTemp[i].startsWith(temp[i].toUpperCase()))
                            {
                                match = false;
                                break;
                            }
                        }
                        if (match)
                            paletteList.add(new ItemStack(m));
                    }
                }
            }
        } else
        {
            if (sortedList.isEmpty())
                SortAllItems();

            paletteList = sortedList;
        }

        paletteList.removeIf(itemStack -> ShopUtil.findItemFromShop(this.shopName, new ItemStack(itemStack.getType())) != -1);

        return paletteList;
    }

    private void ShowItems()
    {
        for (int i = 0; i < 45; i++)
        {
            try
            {
                int idx = i + ((currentPage - 1) * 45);
                if (idx >= paletteList.size()) break;

                ItemStack btn = paletteList.get(idx);
                ItemMeta btnMeta = btn.getItemMeta();

                String lastName = btn.getType().name();
                int subStrIdx = lastName.lastIndexOf('_');
                if (subStrIdx != -1)
                    lastName = lastName.substring(subStrIdx);

                if (btnMeta != null)
                {
                    String[] lore = papi(player,t("PALETTE.LORE").replace("{item}", lastName.replace("_", ""))).split("\n");
                    btnMeta.setLore(new ArrayList<>(Arrays.asList(lore)));
                    btn.setItemMeta(btnMeta);
                }

                inventory.setItem(i, btn);
            } catch (Exception ignored)
            {
            }
        }
    }

    private void SortAllItems()
    {
        ArrayList<ItemStack> allItems = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (m.isAir())
                continue;

            if (m.isItem())
                allItems.add(new ItemStack(m));
        }

        allItems.sort(((Comparator<ItemStack>) (o1, o2) ->
        {
            if (o1.getType().getMaxDurability() > 0 && o2.getType().getMaxDurability() > 0)
                return 0;
            else if (o1.getType().getMaxDurability() > 0)
                return -1;
            else if (o2.getType().getMaxDurability() > 0)
                return 1;

            int isEdible = Boolean.compare(o2.getType().isEdible(), o1.getType().isEdible());
            if (isEdible != 0)
                return isEdible;

            int isSolid = Boolean.compare(o2.getType().isSolid(), o1.getType().isSolid());
            if (isSolid != 0)
                return isSolid;

            int isRecord = Boolean.compare(o2.getType().isRecord(), o1.getType().isRecord());
            if (isRecord != 0)
                return isRecord;

            return 0;
        }).thenComparing(ItemPalette::GetArmorType).thenComparing(ItemPalette::GetSortName));

        sortedList = allItems;
    }

    private static String GetSortName(ItemStack stack)
    {
        String ret = stack.getType().name();

        int idx = ret.lastIndexOf('_');
        //int idx = ret.indexOf('_');
        if (idx != -1)
            ret = ret.substring(idx);

        return ret;
    }

    private static int GetArmorType(ItemStack stack)
    {
        String name = stack.getType().name();
        if (name.contains("HELMET"))
            return 0;
        if (name.contains("CHESTPLATE"))
            return 1;
        if (name.contains("LEGGINGS"))
            return 2;
        if (name.contains("BOOTS"))
            return 3;
        if (name.contains("TURTLE_SHELL"))
            return 4;

        return 5;
    }

    private String GetItemLastName(ItemStack iStack)
    {
        String itemName = iStack.getType().name();
        int idx = itemName.lastIndexOf('_');
        if (idx != -1)
            itemName = itemName.substring(idx);

        return itemName.replace("_", "");
    }

    private void CloseUI()
    {
        DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
    }

    private void MovePage(boolean isLeft, boolean isRight)
    {
        int targetPage = currentPage;
        if (isLeft)
        {
            targetPage -= 1;
            if (targetPage < 1) targetPage = maxPage;
        } else if (isRight)
        {
            targetPage += 1;
            if (targetPage > maxPage) targetPage = 1;
        }

        if(targetPage == currentPage)
            return;

        DynaShopAPI.openItemPalette(player, shopName, shopSlotIndex, targetPage, this.search);
    }

    private void AddAll()
    {
        if(paletteList.isEmpty())
            return;

        int targetSlotIdx;
        for (int i = 0; i < 45; i++)
        {
            if (inventory.getItem(i) != null)
            {
                //noinspection ConstantConditions
                Material material = inventory.getItem(i).getType();
                if (material == Material.AIR)
                    continue;

                ItemStack itemStack = new ItemStack(material); // UI요소를 그대로 쓰는 대신 새로 생성.

                int existSlot = ShopUtil.findItemFromShop(shopName, itemStack);
                if (-1 != existSlot) // 이미 상점에 등록되어 있는 아이템 무시
                    continue;

                targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);

                ShopUtil.addItemToShop(shopName, targetSlotIdx, itemStack, 1, 1, 0.01, -1, 10000, 10000);
            }
        }
        DynaShopAPI.openShopGui(player, shopName, 1);
    }

    private void OnClickSearch(boolean isLeft, boolean isRight)
    {
        if (isLeft)
        {
            player.closeInventory();

            DynamicShop.userTempData.put(player.getUniqueId(), "waitforPalette");
            OnChat.WaitForInput(player);

            player.sendMessage(DynamicShop.dsPrefix(player) + papi(player,t("MESSAGE.SEARCH_ITEM")));
        } else if (isRight)
        {
            if(!search.isEmpty())
                DynaShopAPI.openItemPalette(player, shopName, shopSlotIndex, currentPage, "");
        }
    }

    private void OnClickItem(boolean isLeft, boolean isRight, boolean isShift, ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR)
            return;

        // 인자로 들어오는 item은 UI요소임
        ItemStack itemStack = new ItemStack(item.getType());

        if (isLeft)
        {
            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex,0, itemStack, 10, 10, 0.01, -1, 10000, 10000, -1);
        } else if (isRight)
        {
            int targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true);
            DynamicShop.userInteractItem.put(player.getUniqueId(), shopName + "/" + targetSlotIdx + 1);
            ShopUtil.addItemToShop(shopName, targetSlotIdx, itemStack, -1, -1, -1, -1, -1, -1);

            DynaShopAPI.openShopGui(player, shopName, targetSlotIdx / 45 + 1);
        }
    }

    private void OnClickUserItem(boolean isLeft, boolean isRight, ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR)
            return;

        if (isLeft)
        {
            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, 0, item, 10, 10, 0.01, -1, 10000, 10000, -1);
        } else if (isRight)
        {
            ShopUtil.addItemToShop(shopName, shopSlotIndex, item, -1, -1, -1, -1, -1, -1);

            DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1);
        }
    }
}
