package com.hotmail.AdrianSRJose.AnniPro.itemMenus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.hotmail.AdrianSRJose.AnniPro.itemMenus.ItemMenu.Size;

public class BookItemMenu {
	private final Map<Integer, BookPage> pages = new HashMap<Integer, BookPage>();
	private final List<numerableMenuItem> realItems = new ArrayList<numerableMenuItem>();
	private /* final */ MenuItem[] optionsItem;
	private final BookPage mainPage;

	public BookItemMenu(String title, List<MenuItem> icons, MenuItem[] optionsItem, boolean showPage, boolean fitSize,
			NextMenuItem nextButton, ReturnMenuItem backButton) {
		this.optionsItem = optionsItem;
		//
		int pag = 0;
		//
		int i = 0;
		for (MenuItem item : icons) {
			if (item != null) {
				if (i == 45) {
					i = 0;
					pag += 1;
				}
				//
				realItems.add(new numerableMenuItem(item, pag));
				i++;
			}
		}

		// set Main Page
		mainPage = new BookPage(title + (showPage ? " | Page 1" : ""), fitSize ? Size.fit(icons.size()) : Size.SIX_LINE,
				0);

		// put Pages
		for (int g = 0; g < pag; g++) {
			pages.put(Integer.valueOf(g), new BookPage(title + (showPage ? " | Page " + (g + 2) : ""),
					fitSize ? Size.fit(icons.size()) : Size.SIX_LINE, g));
		}

		for (BookPage pagina : pages.values()) {
			// --------------------------------------------------------//
			int posan = (pagina.getPageNumber() - 1);
			//
			if (posan >= 0) {
				ItemMenu returnMenu = pages.get(Integer.valueOf(posan));
				//
				if (returnMenu != null) {
					ReturnMenuItem rs = backButton != null ? backButton : new ReturnMenuItem(returnMenu);
					rs.setReturnMenu(returnMenu);
					pagina.setItem((Size.getLastSlot(mainPage.getSize()) - 2), rs);
				}
			} else {
				ReturnMenuItem rs = backButton != null ? backButton : new ReturnMenuItem(mainPage);
				rs.setReturnMenu(mainPage);
				pagina.setItem((Size.getLastSlot(mainPage.getSize()) - 2), rs);
			}
			// --------------------------------------------------------//
			//
			// --------------------------------------------------------//
			int posnext = (pagina.getPageNumber() + 1);
			ItemMenu nextMenu = pages.get(Integer.valueOf(posnext));
			//
			if (nextMenu != null) {
				NextMenuItem set = nextButton != null ? nextButton : new NextMenuItem(nextMenu);
				set.setNextMenu(nextMenu);
				pagina.setItem((Size.getLastSlot(mainPage.getSize()) - 1), set);
			}

			// --------------------------------------------------------//
			//
		}
		//
		for (int x = 0; x < this.optionsItem.length; x++) {
			final MenuItem set = this.optionsItem[x];
			int slot = (45 + x);
			//
			if (set != null && slot < 52) {
				mainPage.setItem(slot, set);
				//
				for (BookPage pagina : pages.values())
					if (pagina != null)
						pagina.setItem(slot, set);
			}
		}
		//
		if (pag > 0) {
			NextMenuItem set = nextButton != null ? nextButton : new NextMenuItem(pages.get(0));
			set.setNextMenu(pages.get(0));
			mainPage.setItem((Size.getLastSlot(mainPage.getSize()) - 1), set);
		}
		//
		//
		final List<BookPage> realPagesNumber = new ArrayList<BookPage>();
		realPagesNumber.add(mainPage);
		for (BookPage f : pages.values())
			if (f != null)
				realPagesNumber.add(f);
		//
		//
		for (int x = 0; x < realPagesNumber.size(); x++) {
			BookPage page = realPagesNumber.get(Integer.valueOf(x));
			//
			if (page != null) {
				int g = 0;
				for (numerableMenuItem item : getItemsFromPage(x)) {
					if (item != null) {
						page.setItem(g, item);
						g++;
					}
				}
			}
		}
	}

	public void setOptionItems(final MenuItem[] newOptionsItem) {
		if (newOptionsItem == null || newOptionsItem.length <= 0) {
			return;
		}

		this.optionsItem = newOptionsItem;
		for (int x = 0; x < this.optionsItem.length; x++) {
			final MenuItem set = this.optionsItem[x];
			int slot = (45 + x);
			//
			if (set != null && slot < 52) {
				mainPage.setItem(slot, set);
				//
				for (BookPage pagina : pages.values())
					if (pagina != null)
						pagina.setItem(slot, set);
			}
		}
	}

	public BookItemMenu(String title, List<MenuItem> icons, boolean showPage, boolean fitSize) {
		this(title, icons, new MenuItem[] {}, showPage, fitSize, null, null);
	}

	private List<numerableMenuItem> getItemsFromPage(int page) {
		final List<numerableMenuItem> tor = new ArrayList<numerableMenuItem>();
		for (numerableMenuItem item : realItems)
			if (item != null)
				if (item.getPage() == page)
					tor.add(item);
		return tor;
	}

	public void open(final Player p) {
		mainPage.open(p);
	}

	public Map<Integer, BookPage> getPages() {
		return pages;
	}

	public BookPage getMainPage() {
		return mainPage;
	}

	public Collection<BookPage> getPageList() {
		return pages.values();
	}

	public class BookPage extends ItemMenu {
		private final int number;

		//
		public BookPage(String name, Size size, int pageNumber) {
			super(name, size);
			number = pageNumber;
		}

		//
		public int getPageNumber() {
			return number;
		}
	}

	private class numerableMenuItem extends MenuItem {
		private final int page;
		private final MenuItem original;

		public numerableMenuItem(MenuItem item, int page) {
			super(item.getDisplayName(), item.getIcon(), item.getLoreArray());
			this.page = page;
			original = item;
		}

		public int getPage() {
			return page;
		}

		@Override
		public void onItemClick(ItemClickEvent event) {
			if (original != null)
				original.onItemClick(event);
		}
	}
}
