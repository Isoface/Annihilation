package com.hotmail.AdrianSRJose.AnniPro.itemMenus.RegeneratingBlockMenu;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolColor;
import com.hotmail.AdrianSR.core.util.itemstack.wool.WoolItemStack;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.GameMap;
import com.hotmail.AdrianSRJose.AnniPro.anniMap.RegeneratingBlock;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ActionMenuItem;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickEvent;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemClickHandler;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;
import com.hotmail.AdrianSRJose.AnniPro.itemMenus.MenuItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

public class PrincipalPage {
	private final Player owner;
	//
	private boolean randomDrop = false;
	private Material mat;
	private final Integer dataVal = -1;
	private boolean regenerate = true;
	private boolean cobbleReplace = true;
	private boolean naturalBreak = false;
	private Integer time = null;
	private TimeUnit unit = null;
	private Integer xp = Integer.valueOf(0);
	private Material product = null;
	private String amount = null;
	private Integer productData = -1;
	private String effect = null;
	//
	// --- Menu
	private final ItemMenu rc = new ItemMenu ( 
			ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Regenerating Block Creator", Size.FOUR_LINE);
	// -------------------------------------------------------------------------------------------
	//
	// --- Items
	private ActionMenuItem selectMaterial;
	private ActionMenuItem regenOrUnbreakable;
	//
	private ActionMenuItem selectTime;
	private ActionMenuItem selectAmmount;
	//
	private ActionMenuItem selectProduct;
	private ActionMenuItem selectDatavalue;
	private ActionMenuItem replaceBy;
	private ActionMenuItem selectDropType;
	private ActionMenuItem selectXp;
	private ActionMenuItem selectEffect;
	//
	private SaveRegeneratingBlock save;
	private SaveRegeneratingBlock saveUnbreakable;
	private ActionMenuItem StartOver;
	// -------------------------------------------------------------------------------------------

	public PrincipalPage(final Player p) {
		owner = p;
		make(false);
	}

	private void make(boolean reset) {
		MenuItem currentMaterial = null;
		MenuItem regeOrUnbr = null;
		boolean unbreakable = false;
		//
		boolean randomAmmount = false;
		int min = 1;
		int max = 3;
		MenuItem ammIt = null;
		//
		MenuItem currentProduct = null;
		MenuItem clockRegenerateTime = null;
		MenuItem currentNaturalOrUnnatural = null;
		MenuItem replaceMaterialSelected = null;
		MenuItem currentSelectedXp = null;
		MenuItem currentSelectedDataValue = null;
		MenuItem currentDropEffect = null;
		//
		MenuItem finalProduct = null;
		//
		if (reset) {
			rc.clearAllItems();
			//
			selectMaterial = null;
			regenOrUnbreakable = null;
			selectTime = null;
			selectAmmount = null;
			selectProduct = null;
			selectDatavalue = null;
			replaceBy = null;
			selectDropType = null;
			selectXp = null;
			selectEffect = null;
			save = null;
			saveUnbreakable = null;
			StartOver = null;
			//
			mat = null;
			regenerate = true;
			cobbleReplace = true;
			naturalBreak = false;
			time = null;
			unit = null;
			xp = Integer.valueOf(0);
			product = null;
			amount = null;
			productData = -1;
			effect = null;
		}
		//
		final Object[] datas = getDatas(owner);
		if (datas != null) {
			// -------- Data 1 --------------
			final Object d1 = datas[1];
			if (d1 != null && d1 instanceof Material) {
				Material mat = (Material) d1;
				this.mat = mat;
				currentMaterial = new MenuItem(
						ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Current Material Selected: " 
				+ ChatColor.GOLD + ChatColor.BOLD.toString ( ) + mat.name(),
						new ItemStack ( mat , 1 ));
			}
			// ------------------------------
			// -------- Data 2 --------------
			final Object d2 = datas[2];
			if (d2 != null && d2 instanceof String) {
				String i = (String) d2;
				ItemStack mcru = null;
				String nrdsa = "";
				//
				if (i.equals("key_-Unbreakable-_key")) {
					mcru = new ItemStack(Material.BEDROCK, 1);
					nrdsa = ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Your Block is: " 
							+ ChatColor.GOLD + ChatColor.BOLD.toString ( ) +"Unbreakable";
					unbreakable = true;
					regenerate = false;
				} else if (i.equals("key_-Regenerable-_key")) {
					mcru = new ItemStack(Material.GOLD_ORE, 1);
					nrdsa = ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Your Block is: " +
							ChatColor.GOLD + ChatColor.BOLD.toString ( ) + "Regenerable";
					regenerate = true;
				}
				//
				regeOrUnbr = new MenuItem(nrdsa, mcru);
			}
			// ------------------------------
			// -------- Data 3 --------------
			final Object d3 = datas[3];
			if (d3 != null && d3 instanceof String) /// Random Amount Drop ?
			{
				String s = (String) d3;
				//
				if (s.equalsIgnoreCase("true"))
					randomAmmount = true;
				else if (s.equalsIgnoreCase("false"))
					randomAmmount = false;
				//
				randomDrop = randomAmmount;
			}
			// ------------------------------
			// -------- Data 4 --------------
			final Object d4 = datas[4];
			if (d4 != null && d4 instanceof Integer) {
				Integer i = (Integer) d4;
				ItemStack st = new ItemStack(Material.STICK, i.intValue());
				//
				ammIt = new MenuItem( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + ""
						+ "Current Selected Amount to Drop: §6§l" + i, st);
				//
				if (!randomDrop) {
					amount = String.valueOf(i.intValue());
				}
			}
			// ------------------------------
			// -------- Data 5 --------------
			final Object d5 = datas[5];
			if (d5 != null && d5 instanceof Integer[]) {
				Integer[] minMax = (Integer[]) d5;
				//
				min = minMax[0] != null ? minMax[0] : min;
				max = minMax[1] != null ? minMax[1] : max;
				//
				if (randomDrop) {
					amount = "RANDOM(" + min + "," + max + ")";
				}
			}
			// ------------------------------
			// -------- Data 6 --------------
			final Object d6 = datas[6]; // Time
			// ------------------------------
			// -------- Data 7 --------------
			final Object d7 = datas[7];
			if (d7 != null && d7 instanceof String && d6 != null && d6 instanceof Integer) // Time Unit
			{
				time = (Integer) d6;
				unit = TimeUnit.valueOf((String) d7);
				//
				clockRegenerateTime = new MenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Current Regenerate Time Selected: §6§l"
						+ ((Integer) d6).intValue() + " " + ChatColor.GOLD + ChatColor.BOLD.toString ( ) + (String) d7, new ItemStack(Material.WATCH, 1));
			}
			// ------------------------------
			// -------- Data 8 --------------
			final Object d8 = datas[8];
			if (d8 != null && d8 instanceof Material) {
				Material pro = (Material) d8;
				product = pro;
				currentProduct = new MenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
					+ "Current Product Selected: §6§l" + pro.name(), new ItemStack ( pro , 1 ));
			}
			// ------------------------------
			// -------- Data 9 --------------
			final Object d9 = datas[9];
			if (d9 != null && d9 instanceof Boolean) {
				boolean nb = ((Boolean) d9).booleanValue();
				currentNaturalOrUnnatural = new MenuItem(
						ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
						+ "Current Selected Drop Type: §6§l" + (nb ? "Natural" : "Unnatural"),
						new ItemStack((nb ? Material.GRASS : Material.CHEST), 1));
				//
				naturalBreak = nb;
			}
			// ------------------------------
			// -------- Data 10 --------------
			final Object d10 = datas[10];
			if (d10 != null && d10 instanceof Boolean) {
				boolean rwc = ((Boolean) d10).booleanValue();
				replaceMaterialSelected = new MenuItem(
						ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + 
						"Your block will be replaced by: §6§l" + (rwc ? "Cobblestone" : "Air"),
						new ItemStack((rwc ? Material.COBBLESTONE : Material.GLASS), 1));
				//
				cobbleReplace = rwc;
			}
			// ------------------------------
			// -------- Data 11 --------------
			final Object d11 = datas[11];
			if (d11 != null && d11 instanceof Integer) {
				int xp = ((Integer) d11).intValue();
				this.xp = (Integer) d11;
				currentSelectedXp = new MenuItem( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
				+ "Current Selected Xp: §6§l" + xp,
						new ItemStack(Material.GLOWSTONE_DUST, 1));
			}
			// ------------------------------
			// -------- Data 12 --------------
			final Object d12 = datas[12];
			if (d12 != null && d12 instanceof Integer) {
				int datavalue = ((Integer) d12).intValue();
				this.productData = (Integer) d12;
				currentSelectedDataValue = new MenuItem( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
				+ "Current Selected Product Data: §6§l" + datavalue,
						new ItemStack(Material.INK_SACK, 1, (short) 4));
			}
			// ------------------------------
			// -------- Data 13 --------------
			final Object d13 = datas[13];
			if (d13 != null && d13 instanceof String) {
				String eff = (String) d13;
				//
				effect = eff.equalsIgnoreCase("Gravel") ? eff : null;
				//
				currentDropEffect = new MenuItem( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
				+ "Current Selected Drop Effect: §6§l" + eff,
						new ItemStack(effect != null ? Material.GRAVEL : Material.THIN_GLASS, 1));
			}
		}
		//
		// -- Get Final Product Icon -- //
		if (product != null && productData != null && amount != null && effect == null) {
			int amm = 1;
			if (!this.randomDrop) {
				amm = Integer.parseInt(amount);
			}
			//
			finalProduct = new MenuItem( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
			+ "Final Product",
					new ItemStack(product, amm, (short) productData.intValue()));
		}
		// -----------------------------//
		//
		//
		// -------------------------------------------------------------------------------------------//
		// -- Get Final Regenerating Block
		// /////////////////////////////////////////////////////////////
		if (regenerate) {
			if (mat != null && time != null && unit != null) {
				if (effect == null) {
					if (product != null && productData != null && amount != null) {
						RegeneratingBlock block = new RegeneratingBlock(mat, dataVal.intValue(), regenerate,
								cobbleReplace, naturalBreak, time.intValue(), unit, xp.intValue(), product, amount,
								productData.intValue(), effect, (byte) 0);
						if (block != null) {
							save = new SaveRegeneratingBlock(block);
						}
					}
				} else if (effect != null) {
					RegeneratingBlock block = new RegeneratingBlock(mat, dataVal.intValue(), regenerate, cobbleReplace,
							naturalBreak, time.intValue(), unit, xp.intValue(), null, null, -1, effect, (byte) 0);
					if (block != null) {
						save = new SaveRegeneratingBlock(block);
					}
				}
			}
		} else {
			if (mat != null) {
				RegeneratingBlock block = new RegeneratingBlock(mat, dataVal.intValue(), false, false, false, 0, null,
						0, null, null, 0, null, (byte) 0);
				if (block != null) {
					saveUnbreakable = new SaveRegeneratingBlock(block);
				}
			}
		}
		// --
		// //////////////////////////////////////////////////////////////////////////////////////////
		//
		//
		//
		// -------------------------------------------------------------------------------------------//
		// ----------- Make Items
		// -------------------------------------------------------------------//
		if (selectMaterial == null) {
			selectMaterial = new ActionMenuItem( ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
			+  "Select the RegenerationBlock Type", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					final Player p = eve.getPlayer();
					if (p.isOnline())
						new SelectMaterialMenu(p, rc, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Select a Material").open();
				}
			}, new ItemStack(Material.GRASS), new String[] { "", "§7§lSelect the block §6§lType.", });
		}
		//
		if (regenOrUnbreakable == null) {
			regenOrUnbreakable = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
			+ "Regenerable or Unbreakable?", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					final Player p = eve.getPlayer();
					//
					if (p != null && p.isOnline())
						new TwoOptionsMenu(Material.GOLD_ORE, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) + "Regenerable",
								ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
								+ "Now Your Block is: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
								+ "Regenerable", Material.BEDROCK, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
								+ "Unbreakable",
								ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )
								+ "Now Your Block is: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
								+ "Unbreakable", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
								+ "Regenerable or Unbreakable?", true)
										.open(p);
				}
			}, new ItemStack(Material.IRON_PICKAXE, 1),
					new String[] { "", "§7§lWould you like this ", "§7§lblock to be just §6§lunbreakable§7§l,",
							"§7§lOr would you like it to §6§lregenerate?", "",
							"§8§lRegenerable §7§l= §6§l(Auto regenerable)",
							"§8§lUnbreakable §7§l= §6§l(You can't break it)", });
		}
		//
		if (selectAmmount == null) {
			selectAmmount = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
			+ "Select the Amount to Drop to the Player", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					final Player p = eve.getPlayer();
					//
					if (p != null && p.isOnline()) {
						new RandomOrNormalAmmountMenu(rc).open(p);
					}
				}
			}, new ItemStack(Material.STICK),
					new String[] { "", "§7§lHow much product", "§7§lwould you like to §6§lgive?" });
		}
		//
		if (selectTime == null) {
			selectTime = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( ) 
			+ "Select the Regenerate Time", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					new RegenerateTimeSelectMenu(eve.getPlayer()).open(eve.getPlayer());
				}
			}, new ItemStack(Material.WATCH, 1), new String[] { "", "§7§lHow long would you",
					"§7§llike this block to remain", "§7§lbroken before it is §6§lregenerated?" });
		}
		//
		if (selectProduct == null) {
			selectProduct = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Select a Product to Drop", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					new SelectProductMenu(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Select a Product", eve.getPlayer(), rc).open();
				}
			}, new ItemStack(Material.APPLE, 1), new String[] { "", "§7§lWhat type of §6§lproduct §7§lwould you like",
					"§7§lto §6§lgive §7§lwhen this §6§lblock is broken?" });
		}
		//
		if (selectDropType == null) {
			selectDropType = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Natural or Unnatural Droped?", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					new NaturalUnnaturalMenu().open(eve.getPlayer());
				}
			}, new ItemStack(Material.FISHING_ROD, 1),
					new String[] { "", "§7§lWould you like this block to break",
							"§7§lnaturally §6§l(Natural). §7§lOr would you like the items",
							"§7§lto be added to the players's inventory §6§l(Unnatural)?" });
		}
		//
		if (replaceBy == null) {
			replaceBy = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Replece With Cobblestone or Air?", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					new CobblestoneOrAirReplaceSelectMenu().open(eve.getPlayer());
				}
			}, new ItemStack(Material.COBBLESTONE, 1), new String[] { "", "§7§lWhen it is broken,",
					"§7§lwould you like this block", "§7§lto be replaced by §6§lCobblestone §7§lor §6§lAir?" });
		}
		//
		if (selectXp == null) {
			selectXp = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Select the Xp Amount to Drop to the Player", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					final Player p = eve.getPlayer();
					//
					if (KitUtils.isValidPlayer(p))
						new SelectXPAmmountMenu(p).open(p);
				}
			}, new ItemStack(Material.EXP_BOTTLE, 1),
					new String[] { "", "§7§lHow much XP", "§7§ldo you want to §6§lgive?" });
		}
		//
		if (selectEffect == null) {
			selectEffect = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Select the Drop Effect", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					new GravelOrNoneMenu(eve.getPlayer()).specialOpen();
				}
			}, new ItemStack(Material.FLINT, 1),
					new String[] { "", "§7§lSelect a special", "§6§leffect §7§lwould you like to use." });
		}
		//
		if (selectDatavalue == null) {
			selectDatavalue = new ActionMenuItem(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString ( )+"Select The Product Data", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					final Player p = eve.getPlayer();
					if (KitUtils.isValidPlayer(p)) {
						new DataValueSelectMenu(p).open(p);
					}
				}
			}, new ItemStack(Material.BOOKSHELF, 1),
					new String[] { "", "§7§lSelect the", "§7§lMaterial §6§lData value." });
		}
		//
		if (StartOver == null) {
			StartOver = new ActionMenuItem("§c§lRestart", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent eve) {
					final Player p = eve.getPlayer();
					//
					if (p.isOnline()) {
						final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
						//
						// ------ Set null the all dates --------
						if (ap != null) {
							ap.setData("key-Material-key", null);
							ap.setData("key-Material-key-(verify)", null);
							ap.setData("key-Effect-Gravel-or-None-key(Verify)", null);
							ap.setData("key-Regenreable_or_Unbreakable-key", null);
							ap.setData("key-random-ammount-selected", null);
							ap.setData("key-min-ammount-selected", null);
							ap.setData("key-max-ammount-selected", null);
							ap.setData("key-ammount-selected1", null);
							ap.setData("key-random-min-and-max-to-drop", null);
							ap.setData("key-regenerate-time-selected", null);
							ap.setData("key-regenerate-key-Unit", null);
							ap.setData("key-Product-key", null);
							ap.setData("key-Product-key-(verify)", null);
							ap.setData("key-naturalBreak-key", null);
							ap.setData("key-replace-with-cobblestone-key", null);
							ap.setData("key-xp-ammount-selected-key", null);
							ap.setData("key-datavalue-selected-key", null);
							ap.setData("key-Effect-Gravel-or-None-key", null);
						}
						//
						rc.clearAllItems();
						make(true);
						//
						rc.open(p);
					}
				}
			}, new ItemStack(Material.TNT), new String[] {});
		}
		//
		// -------------------------------------------------------------------------------------------
		// ---- Set Items ---- //
		rc.setItem(10, selectMaterial);
		rc.setItem(11, regenOrUnbreakable);
		//
		if (!unbreakable) {
			rc.setItem(12, selectTime);
			rc.setItem(13, selectEffect); // 23
			rc.setItem(14, selectDropType);
			rc.setItem(15, replaceBy);
			//
			rc.setItem(21, selectXp);
			//
			if (effect == null) {
				rc.setItem(16, selectAmmount);
				rc.setItem(22, selectDatavalue);
				rc.setItem(23, selectProduct); // 13
			}
		}
		//
		if (StartOver != null)
			rc.setItem(34, StartOver);
		if (save != null) {
			rc.setItem(35, save);
		} else if (saveUnbreakable != null) {
			rc.setItem(35, saveUnbreakable);
		}
		// ---------------------//
		//
		/// --- Decoraciones ---
		if (currentMaterial != null)
			rc.setItem(1, currentMaterial);
		//
		if (regeOrUnbr != null)
			rc.setItem(2, regeOrUnbr);
		//
		if (!unbreakable) {
			if (effect == null) {
				if (randomAmmount)
					rc.setItem(7, new MenuItem("§6§lMin/Max §d§lAmount to Drop Selected: §6§l" + min + "/" + max,
							new ItemStack(Material.SEEDS)));
				else if (ammIt != null)
					rc.setItem(7, ammIt);
			}
			//
			//
			if (clockRegenerateTime != null)
				rc.setItem(3, clockRegenerateTime);
			//
			if (currentDropEffect != null)
				rc.setItem(4, currentDropEffect);
			//
			if (currentProduct != null && effect == null)
				rc.setItem(24, currentProduct);
			//
			if (currentNaturalOrUnnatural != null)
				rc.setItem(5, currentNaturalOrUnnatural);
			//
			if (replaceMaterialSelected != null)
				rc.setItem(6, replaceMaterialSelected);
			//
			if (currentSelectedXp != null)
				rc.setItem(20, currentSelectedXp);
			//
			if (currentSelectedDataValue != null && effect == null)
				rc.setItem(31, currentSelectedDataValue);
			//
			//
			if (finalProduct != null)
				rc.setItem(27, finalProduct);
		}
		//
		// ----------------------
	}

	private Object[] getDatas(final Player p) {
		Object[] l = new Object[100];
		//
		final AnniPlayer ap = AnniPlayer.getPlayer(p.getUniqueId());
		if (ap != null) {
			// ---- Data 1 -----------------------------
			Object d1 = ap.getData("key-Material-key"); // key-Material-key
			if (d1 != null && d1 instanceof Material) {
				l[1] = d1;
			}
			// ------------------------------------------
			// ---- Data 2 -----------------------------
			Object d2 = ap.getData("key-Regenreable_or_Unbreakable-key"); // key-Regenreable_or_Unbreakable-key
			if (d2 != null && d2 instanceof String) {
				l[2] = d2;
			}
			// ------------------------------------------
			// ---- Data 3 -----------------------------
			Object d3 = ap.getData("key-random-ammount-selected"); // key-random-ammount-selected
			if (d3 != null && d3 instanceof String) {
				l[3] = d3;
			}
			// ------------------------------------------
			// ---- Data 4 -----------------------------
			Object d4 = ap.getData("key-ammount-selected1"); // key-ammount-selected1
			if (d4 != null && d4 instanceof Integer) {
				l[4] = d4;
			}
			// ------------------------------------------
			// ---- Data 5 -----------------------------
			Object d5 = ap.getData("key-random-min-and-max-to-drop"); // key-random-min-and-max-to-drop
			if (d5 != null && d5 instanceof Integer[]) {
				l[5] = d5;
			}
			// ------------------------------------------
			// ---- Data 6 -----------------------------
			Object d6 = ap.getData("key-regenerate-time-selected"); // key-regenerate-time-selected
			if (d6 != null && d6 instanceof Integer) {
				l[6] = d6;
			}
			// ------------------------------------------
			// ---- Data 7 -----------------------------
			Object d7 = ap.getData("key-regenerate-key-Unit"); // key-regenerate-key-Unit
			if (d7 != null && d7 instanceof String) {
				l[7] = d7;
			}
			// ------------------------------------------
			// ---- Data 8 -----------------------------
			Object d8 = ap.getData("key-Product-key"); // key-Product-key
			if (d8 != null && d8 instanceof Material) {
				l[8] = d8;
			}
			// ------------------------------------------
			// ---- Data 9 -----------------------------
			Object d9 = ap.getData("key-naturalBreak-key"); // key-naturalBreak-key
			if (d9 != null && d9 instanceof Boolean) {
				l[9] = d9;
			}
			// ------------------------------------------
			// ---- Data 10 -----------------------------
			Object d10 = ap.getData("key-replace-with-cobblestone-key"); // key-replace-with-cobblestone-key
			if (d10 != null && d10 instanceof Boolean) {
				l[10] = d10;
			}
			// ------------------------------------------
			// ---- Data 11 -----------------------------
			Object d11 = ap.getData("key-xp-ammount-selected-key"); // key-xp-ammount-selected-key
			if (d11 != null && d11 instanceof Integer) {
				l[11] = d11;
			}
			// ------------------------------------------
			// ---- Data 12 -----------------------------
			Object d12 = ap.getData("key-datavalue-selected-key"); // key-datavalue-selected-key
			if (d12 != null && d12 instanceof Integer) {
				l[12] = d12;
			}
			// ------------------------------------------
			// ---- Data 13 -----------------------------
			Object d13 = ap.getData("key-Effect-Gravel-or-None-key"); // key-Effect-Gravel-or-None-key
			if (d13 != null && d13 instanceof String) {
				l[13] = d13;
			}
		} else
			return null;
		//
		return l;
	}

	private class SaveRegeneratingBlock extends MenuItem {
		private final RegeneratingBlock block;

		//
		public SaveRegeneratingBlock(RegeneratingBlock block) {
			super("§a§lSave", new WoolItemStack ( WoolColor.GREEN , 1 ));
			this.block = block;
		}

		@Override
		public void onItemClick(ItemClickEvent eve) {
			final Player p = eve.getPlayer();
			final AnniPlayer ap = AnniPlayer.getPlayer(p);
			GameMap map = Game.getGameMap();
			//
			if (map == null) {
				p.sendMessage("§cNo Valid Game Map Loaded.");
				eve.setWillClose(true);
				return;
			}
			//
			if (block != null) {
				if (ap != null) {
					ap.setData("key-Material-key", null);
					ap.setData("key-Material-key-(verify)", null);
					ap.setData("key-Regenreable_or_Unbreakable-key", null);
					ap.setData("key-random-ammount-selected", null);
					ap.setData("key-min-ammount-selected", null);
					ap.setData("key-max-ammount-selected", null);
					ap.setData("key-ammount-selected1", null);
					ap.setData("key-random-min-and-max-to-drop", null);
					ap.setData("key-regenerate-time-selected", null);
					ap.setData("key-regenerate-key-Unit", null);
					ap.setData("key-Product-key", null);
					ap.setData("key-Product-key-(verify)", null);
					ap.setData("key-naturalBreak-key", null);
					ap.setData("key-replace-with-cobblestone-key", null);
					ap.setData("key-xp-ammount-selected-key", null);
					ap.setData("key-datavalue-selected-key", null);
					ap.setData("key-Effect-Gravel-or-None-key(Verify)", null);
					ap.setData("key-Effect-Gravel-or-None-key", null);
					//
					if (block.Type == Material.REDSTONE_ORE) {
						Material[] rts = new Material[] { Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE };
						for (Material r : rts) {
							Game.getGameMap().getRegeneratingBlocks()
									.addRegeneratingBlock(new RegeneratingBlock(r, dataVal.intValue(), regenerate,
											cobbleReplace, naturalBreak, time, unit, xp, product, amount, productData,
											effect, (byte) 0));
						}
					} else {
						map.getRegeneratingBlocks().addRegeneratingBlock(block);
					}
					p.sendMessage("§a§lThe §6§lRegeneratingBlock §a§lhas been §6§ladded §a§lcorrectly.");
					//
					rc.clearAllItems();
					make(true);
				}
			} else {
				eve.setWillClose(true);
				p.sendMessage("§cInvalid Regenerating Block");
				return;
			}
			//
			eve.setWillClose(true);
		}
	}

	public void open(boolean remake) {
		if (owner != null && owner.isOnline()) {
			make(remake);
			rc.open(owner);
		}
	}
}
