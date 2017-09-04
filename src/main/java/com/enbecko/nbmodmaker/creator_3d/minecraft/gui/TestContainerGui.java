package com.enbecko.nbmodmaker.creator_3d.minecraft.gui;

import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import com.enbecko.nbmodmaker.creator_3d.minecraft.network.M_AddBlockToBone;
import com.enbecko.nbmodmaker.creator_3d.minecraft.network.M_AddBone;
import com.enbecko.nbmodmaker.creator_3d.minecraft.network.PacketDispatcher;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;

import java.io.IOException;

public class TestContainerGui extends GuiContainer {
    public static final int WIDTH = 180;
    public static final int HEIGHT = 152;

    GuiButton add, addBone;
    GuiTextField x, y, z, gridSize, boneName;
    TE_Editor te_editor;

    //private static final ResourceLocation background = new ResourceLocation(ModTut.MODID, "textures/gui/testcontainer.png");

    public TestContainerGui(TE_Editor tileEntity, TestContainer container) {
        super(container);
        this.te_editor = tileEntity;
    }

    public void initGui() {
        super.initGui();
        int i = this.width / 2;
        int j = this.height / 2;
        System.out.println(this.width + " " + this.height + " " + this.xSize + " " + this.ySize);

        this.boneName = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, i - 130, j - 50, 120, 20);
        this.boneName.setText("name");
        addBone = this.addButton(new GuiButton(1, i + 10, j - 50, 120, 20, "Add Bone"));

        this.x = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, i - 130, j - 20, 80, 20);
        this.x.setFocused(true);
        this.x.setText("1");
        this.y = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, i - 40, j - 20, 80, 20);
        this.y.setText("2");
        this.z = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, i + 50, j - 20, 80, 20);
        this.z.setText("3");
        this.gridSize = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, i - 40, j + 10, 80, 20);
        this.gridSize.setText("8");
        add = this.addButton(new GuiButton(0, i - 40, j + 40, 80, 20, "Add"));
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();
        this.boneName.updateCursorCounter();
        this.x.updateCursorCounter();
        this.y.updateCursorCounter();
        this.z.updateCursorCounter();
        this.gridSize.updateCursorCounter();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == this.add) {
            PacketDispatcher.sendToServer(new M_AddBlockToBone(te_editor.getPos(), 0, Integer.parseInt(this.gridSize.getText()), Integer.parseInt(this.x.getText()), Integer.parseInt(this.y.getText()), Integer.parseInt(this.z.getText()), Blocks.ACACIA_DOOR.getDefaultState()));
        } else if (button == this.addBone) {
            Bone b = te_editor.addBone(0, 0, 0, this.boneName.getText());
            PacketDispatcher.sendToServer(new M_AddBone(te_editor.getPos(), b.getBoneID(), 0, 0, 0, this.boneName.getText()));
        }
    }

    protected void mouseClicked(int x, int y, int btn) throws IOException {
        super.mouseClicked(x, y, btn);
        this.boneName.mouseClicked(x, y, btn);
        this.x.mouseClicked(x, y, btn);
        this.y.mouseClicked(x, y, btn);
        this.z.mouseClicked(x, y, btn);
        this.gridSize.mouseClicked(x, y, btn);
    }

    protected void keyTyped(char par1, int par2) throws IOException {
        super.keyTyped(par1, par2);
        this.boneName.textboxKeyTyped(par1, par2);
        this.x.textboxKeyTyped(par1, par2);
        this.y.textboxKeyTyped(par1, par2);
        this.z.textboxKeyTyped(par1, par2);
        this.gridSize.textboxKeyTyped(par1, par2);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.boneName.drawTextBox();
        this.x.drawTextBox();
        this.y.drawTextBox();
        this.z.drawTextBox();
        this.gridSize.drawTextBox();
    }
}
