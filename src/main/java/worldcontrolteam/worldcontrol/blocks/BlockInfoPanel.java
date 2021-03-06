package worldcontrolteam.worldcontrol.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import worldcontrolteam.worldcontrol.client.ClientUtil;
import worldcontrolteam.worldcontrol.tileentity.TileEntityInfoPanel;
import worldcontrolteam.worldcontrol.utils.WCUtility;

public class BlockInfoPanel extends BlockBasicRotate {
    public static final PropertyBool CONNECTED_UP = PropertyBool.create("up");
    public static final PropertyBool CONNECTED_DOWN = PropertyBool.create("down");
    public static final PropertyBool CONNECTED_LEFT = PropertyBool.create("left");
    public static final PropertyBool CONNECTED_RIGHT = PropertyBool.create("right");
    public static final PropertyBool POWERED = PropertyBool.create("power");
    public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 14);
    private boolean advanced;

    public BlockInfoPanel(Material material, String name, boolean advanced) {
        super(material, name);
        this.advanced = advanced;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);
        WCUtility.getTileEntity(world,pos, TileEntityInfoPanel.class).ifPresent(TileEntityInfoPanel::attemptConnection);
    }

    @Override
    public void registerModels(ModelRegistryEvent event) {
        ClientUtil.registerToNormal(this);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return WCUtility.getTileEntity(worldIn, pos, TileEntityInfoPanel.class).map(tile -> state
                .withProperty(CONNECTED_UP, tile.connectedTo(TileEntityInfoPanel.Side.UP))
                .withProperty(CONNECTED_DOWN, tile.connectedTo(TileEntityInfoPanel.Side.DOWN))
                .withProperty(CONNECTED_LEFT, tile.connectedTo(TileEntityInfoPanel.Side.LEFT))
                .withProperty(CONNECTED_RIGHT, tile.connectedTo(TileEntityInfoPanel.Side.RIGHT))
                .withProperty(POWERED, tile.screen.isPowered())
                .withProperty(COLOR, tile.screen.getColor())
        ).orElse(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(FACING, CONNECTED_UP, CONNECTED_DOWN, CONNECTED_LEFT, CONNECTED_RIGHT, COLOR, POWERED).build();
    }

    @Override
    public TileEntity createTile(World world, int meta) {
        return new TileEntityInfoPanel();
    }

    @Override
    public boolean hasGUI() {
        return false;
    }

    @Override
    public int guiID() {
        return 0;
    }
}