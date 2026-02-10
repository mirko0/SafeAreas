package com.mcodelogic.safeareas.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.lang.Lang;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionType;
import com.mcodelogic.safeareas.utils.KCommandUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RegionCreateCommand extends AbstractPlayerCommand {

    private final RegionManager manager;

    private final RequiredArg<String> type;
    private final RequiredArg<String> name;

    public RegionCreateCommand(RegionManager manager) {
        super("create", manager.getLang().get("CommandDescRegionCreate"));
        this.manager = manager;
        var lang = manager.getLang();
        this.type = this.withRequiredArg("type", lang.get("CommandRegionCreateArgType"), ArgTypes.STRING);
        this.name = this.withRequiredArg("name", lang.get("CommandRegionCreateArgName"), ArgTypes.STRING);
        this.requirePermission(KCommandUtil.permissionFromCommand("region", "create"));
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        String type = this.type.get(commandContext).toUpperCase().trim();
        String name = this.name.get(commandContext);
        String[] allowedTypes = {"GLOBAL", "AREA", "CUBOID"};
        var lang = manager.getLang();
        if (Arrays.stream(allowedTypes).noneMatch(type::equalsIgnoreCase)) {
            commandContext.sendMessage(lang.getMessage("CommandRegionCreateInvalidType"));
            return;
        }
        Region found = manager.getApi().getRegionByName(world.getName(), name);
        if (found != null) {
            commandContext.sendMessage(lang.getMessage("CommandRegionCreateNameExists", name));
            return;
        }
        if (type.equals("GLOBAL")) {
            Collection<Region> allRegionsInWorld = manager.getApi().getAllRegionsInWorld(world);
            boolean globalExists = allRegionsInWorld.stream().anyMatch(region -> region.getType().equals(RegionType.GLOBAL));
            if (globalExists) {
                commandContext.sendMessage(lang.getMessage("CommandRegionCreateGlobalExists"));
                return;
            }
        }

        assert playerComponent != null;

        if (PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, store)) {
            BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
            BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
                try {
                    BlockSelection selection = builderState.getSelection();
                    if (selection == null || !selection.hasSelectionBounds()) {
                        if (!type.equals("GLOBAL")) {
                            commandContext.sendMessage(manager.getLang().getMessage("CommandRegionCreateUseSelectionTool"));
                            return;
                        }
                    }



                    if (type.equals("GLOBAL")) {
                        manager.getApi().createGlobalRegion(world.getName(), name, -1000);
                    }
                    Vector3i min = selection == null ? new Vector3i(0, 0, 0) :selection.getSelectionMin();
                    Vector3i max = selection == null ? new Vector3i(0, 0, 0) : selection.getSelectionMax();
                    if (type.equals("AREA")) {
                        manager.getApi().createAreaRegion(world.getName(), name, min.getX(), min.getZ(), max.getX(), max.getZ(), 1);
                    }
                    if (type.equals("CUBOID")) {
                        manager.getApi().createCuboidRegion(world.getName(), name, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), 1);
                    }

                    Lang createLang = manager.getLang();
                    List<Message> messages = new ArrayList<>();
                    messages.add(createLang.getMessage("CommandRegionCreateSuccessHeader"));
                    messages.add(createLang.getMessage("CommandRegionCreateSuccessName", name));
                    messages.add(createLang.getMessage("CommandRegionCreateSuccessType", type));
                    if (type.equals("GLOBAL")) {
                        messages.add(createLang.getMessage("CommandRegionCreateSuccessBoundsGlobal"));
                    } else {
                        String minStr = type.equals("CUBOID")
                                ? (int) min.getX() + ", " + (int) min.getY() + ", " + (int) min.getZ()
                                : (int) min.getX() + ", " + (int) min.getZ();
                        String maxStr = type.equals("CUBOID")
                                ? (int) max.getX() + ", " + (int) max.getY() + ", " + (int) max.getZ()
                                : (int) max.getX() + ", " + (int) max.getZ();
                        messages.add(createLang.getMessage("CommandRegionCreateSuccessBounds", minStr, maxStr));
                    }
                    commandContext.sendMessage(Message.join(messages.toArray(new Message[0])));
                } catch (Exception e) {
                    e.printStackTrace();
                    commandContext.sendMessage(manager.getLang().getMessage("CommandRegionCreateError"));
                }
            });
        }
    }
}
