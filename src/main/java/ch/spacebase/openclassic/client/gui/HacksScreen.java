package ch.spacebase.openclassic.client.gui;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.gui.GuiScreen;
import ch.spacebase.openclassic.api.gui.widget.Button;
import ch.spacebase.openclassic.api.gui.widget.StateButton;
import ch.spacebase.openclassic.api.render.RenderHelper;
import ch.spacebase.openclassic.client.util.GeneralUtils;

import com.mojang.minecraft.GameSettings;

public final class HacksScreen extends GuiScreen {

	private GuiScreen parent;
	private GameSettings settings;

	public HacksScreen(GuiScreen parent, GameSettings settings) {
		this.parent = parent;
		this.settings = settings;
	}

	public final void onOpen() {
		this.clearWidgets();
		for (int count = 0; count < this.settings.hacks; count++) {
			this.attachWidget(new StateButton(count, this.getWidth() / 2 - 155 + count % 2 * 160, this.getHeight() / 6 + 24 * (count >> 1), 155, 20, this, this.settings.getHackName(count)));
			this.getWidget(count, StateButton.class).setState(this.settings.getHackValue(count));
		}
		
		this.attachWidget(new Button(100, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, this, OpenClassic.getGame().getTranslator().translate("gui.done")));
	}

	public final void onButtonClick(Button button) {
		if (button.isActive()) {
			if (button.getId() < 100) {
				this.settings.toggleHack(button.getId());
				((StateButton) button).setState(this.settings.getHackValue(button.getId()));
			}

			if (button.getId() == 100) {
				OpenClassic.getClient().setCurrentScreen(this.parent);
			}
		}
	}

	public final void render() {
		if(GeneralUtils.getMinecraft().ingame) {
			RenderHelper.getHelper().color(0, 0, this.getWidth(), this.getHeight(), 1610941696, -1607454624);
		} else {
			RenderHelper.getHelper().drawDefaultBG();
		}
		
		RenderHelper.getHelper().renderText(OpenClassic.getGame().getTranslator().translate("gui.hacks"), this.getWidth() / 2, 20);
		super.render();
	}
}
