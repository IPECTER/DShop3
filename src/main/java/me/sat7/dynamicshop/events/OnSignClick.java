package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.constants.Constants;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.sat7.dynamicshop.constants.Constants.*;
import static me.sat7.dynamicshop.utilities.LangUtil.t;

public class OnSignClick implements Listener
{
    // 생성
    @EventHandler
    public void onSignChange(SignChangeEvent e)
    {
        if (!e.getPlayer().hasPermission(P_ADMIN_CREATE_SIGN)) return;

        //noinspection ConstantConditions
        if (e.getLine(0).equalsIgnoreCase("[dshop]")
            || e.getLine(0).equalsIgnoreCase("[ds]")
            || e.getLine(0).equalsIgnoreCase("[dynamicshop]"))
        {
            String signId = CreateID(e.getBlock());

            if (e.getLine(1).length() == 0)
            {
                e.setLine(1, "Error");
                e.setLine(2, "shop name is null");
                e.getBlock().getState().update();
                return;
            }

            if (!ShopUtil.shopConfigFiles.containsKey(e.getLine(1)))
            {
                e.setLine(1, "Error");
                e.setLine(2, "No shop");
                e.setLine(3, "with that name");
                e.getBlock().getState().update();
                return;
            }

            e.setLine(0, e.getLine(3));
            e.setLine(1, "§a" + e.getLine(1));
            e.setLine(3, "");
            e.getBlock().getState().update();

            DynamicShop.ccSign.get().set(signId + ".shop", ChatColor.stripColor(e.getLine(1)));

            Block tempBlock = e.getBlock();
            Block blockBehind = null;
            if (tempBlock.getState() instanceof Sign)
            {
                BlockData data = tempBlock.getBlockData();
                if (data instanceof Directional)
                {
                    Directional directional = (Directional) data;
                    blockBehind = tempBlock.getRelative(directional.getFacing().getOppositeFace());
                }
            }
            if (blockBehind != null)
            {
                DynamicShop.ccSign.get().set(signId + ".attached", CreateID(blockBehind));
            } else
            {
                e.setLine(1, "Error");
                e.setLine(2, "Sign must be ");
                e.setLine(3, "placed on wall");
                e.getBlock().getState().update();
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Err. Sign must be placed on wall. " + signId);
                return;
            }

            try
            {
                String shop = ChatColor.stripColor(e.getLine(1));
                String mat = ChatColor.stripColor(e.getLine(2)).toUpperCase();
                int i = ShopUtil.findItemFromShop(shop, new ItemStack(Material.getMaterial(mat)));

                e.setLine(2, ShopUtil.shopConfigFiles.get(shop).get().getString(i + ".mat"));

                DynamicShop.ccSign.get().set(signId + ".mat", mat);
            } catch (Exception exception)
            {
                e.setLine(2, "");
            }

            DynamicShop.ccSign.save();
        }
    }

    // 상호작용
    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (e.getClickedBlock().getType().toString().contains("WALL_SIGN"))
            {
                Sign s = (Sign) e.getClickedBlock().getState();
                String signId = CreateID(e.getClickedBlock());

                // 정보가 없음
                if (!DynamicShop.ccSign.get().contains(signId) &&
                        s.getLine(1).length() > 0 &&
                        ShopUtil.shopConfigFiles.containsKey(ChatColor.stripColor(s.getLine(1))))
                {
                    // 재생성 시도
                    if (e.getPlayer().hasPermission(P_ADMIN_CREATE_SIGN))
                    {
                        String shop = ChatColor.stripColor(s.getLine(1));
                        DynamicShop.ccSign.get().set(signId + ".shop", shop);
                        s.setLine(0, "");
                        s.setLine(1, "§a" + s.getLine(1));

                        try
                        {
                            String mat = ChatColor.stripColor(s.getLine(2)).toUpperCase();
                            int i = ShopUtil.findItemFromShop(shop, new ItemStack(Material.getMaterial(mat)));
                            s.setLine(2, ShopUtil.shopConfigFiles.get(shop).get().getString(i + ".mat"));
                            DynamicShop.ccSign.get().set(signId + ".mat", mat);
                        } catch (Exception exception)
                        {
                            s.setLine(2, "");
                        }

                        s.update();
                        DynamicShop.ccSign.save();
                    } else
                    {
                        return;
                    }
                }

                if (DynamicShop.ccSign.get().contains(signId) && !DynamicShop.ccSign.get().contains(signId + ".attached"))
                {
                    Block tempBlock = e.getClickedBlock();
                    Block blockBehind = null;
                    if (tempBlock != null && tempBlock.getState() instanceof Sign)
                    {
                        BlockData data = tempBlock.getBlockData();
                        if (data instanceof Directional)
                        {
                            Directional directional = (Directional) data;
                            blockBehind = tempBlock.getRelative(directional.getFacing().getOppositeFace());
                        }
                    }
                    DynamicShop.ccSign.get().set(signId + ".attached", CreateID(blockBehind));
                    DynamicShop.ccSign.save();
                }

                String shopName = DynamicShop.ccSign.get().getString(signId + ".shop");
                if (shopName == null || shopName.length() == 0) return;

                // 상점 존재 확인
                if (ShopUtil.shopConfigFiles.containsKey(shopName))
                {
                    if (p.getGameMode() == GameMode.CREATIVE && !p.hasPermission(Constants.P_ADMIN_CREATIVE))
                    {
                        p.sendMessage(DynamicShop.dsPrefix(p) + LangUtil.papi(p, t( "ERR.CREATIVE")));
                        return;
                    }

                    if(!p.hasPermission(P_ADMIN_SHOP_EDIT))
                        e.setCancelled(true);
                    else
                    {
                        String itemName = p.getInventory().getItemInMainHand().getType().name();
                        if(itemName.contains("INK_SAC") || itemName.contains("_DYE"))
                        return;
                    }


                    //권한 확인
                    String permission = ShopUtil.shopConfigFiles.get(shopName).get().getString("Options.permission");
                    if (permission != null && permission.length() > 0)
                    {
                        if (!p.hasPermission(permission) && !p.hasPermission(permission + ".buy") && !p.hasPermission(permission + ".sell"))
                        {
                            p.sendMessage(DynamicShop.dsPrefix(p) + LangUtil.papi(p, t( "ERR.NO_PERMISSION")));
                            return;
                        }
                    }

                    try
                    {
                        int idx = ShopUtil.findItemFromShop(shopName, new ItemStack(Material.getMaterial(DynamicShop.ccSign.get().getString(signId + ".mat"))));

                        if (idx != -1)
                        {
                            DynamicShop.userTempData.put(p.getUniqueId(), "sign");

                            DynaShopAPI.openItemTradeGui(p, shopName, String.valueOf(idx));
                        } else
                        {
                            DynamicShop.userTempData.put(p.getUniqueId(), "sign");
                            DynaShopAPI.openShopGui(p, shopName, 1);
                        }
                    } catch (Exception exception)
                    {
                        DynamicShop.userTempData.put(p.getUniqueId(), "sign");
                        DynaShopAPI.openShopGui(p, shopName, 1);
                    }
                }
            }
        }
    }

    // 파괴
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        Block b = e.getBlock();
        String eventID = CreateID(b);

        for (String s : DynamicShop.ccSign.get().getKeys(false))
        {
            if (s.equals(eventID))
            {
                if (!e.getPlayer().hasPermission(P_ADMIN_DESTROY_SIGN))
                {
                    e.setCancelled(true);
                } else
                {
                    DynamicShop.ccSign.get().set(eventID, null);
                    DynamicShop.ccSign.save();
                }
                break;
            }

            if (eventID.equals(DynamicShop.ccSign.get().getString(s + ".attached")))
            {
                if (!e.getPlayer().hasPermission(P_ADMIN_DESTROY_SIGN))
                {
                    e.setCancelled(true);
                } else
                {
                    DynamicShop.ccSign.get().set(s, null);
                    DynamicShop.ccSign.save();
                }
                break;
            }
        }
    }

    // 상점 표지판이 폭발하는것 방지
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        List<Block> b = event.blockList();

        for (Block bl : b)
        {
            if (bl.getType().toString().contains("WALL_SIGN"))
            {
                if (DynamicShop.ccSign.get().contains(CreateID(bl)))
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    // 상점 표지판이 불타는것 방지
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e)
    {
        ArrayList<Block> signList = new ArrayList<>();
        signList.add(e.getBlock());
        signList.add(e.getBlock().getRelative(BlockFace.EAST));
        signList.add(e.getBlock().getRelative(BlockFace.WEST));
        signList.add(e.getBlock().getRelative(BlockFace.NORTH));
        signList.add(e.getBlock().getRelative(BlockFace.SOUTH));

        for (Block b : signList)
        {
            if (b.getType().toString().contains("WALL_SIGN"))
            {
                if (DynamicShop.ccSign.get().contains(CreateID(b)))
                {
                    e.setCancelled(true);
                }
            }
        }
    }

    private String CreateID(Block attachedBlock)
    {
        int x = attachedBlock.getX();
        int y = attachedBlock.getY();
        int z = attachedBlock.getZ();
        return attachedBlock.getWorld() + "_" + x + "_" + y + "_" + z;
    }
}
