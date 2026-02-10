package com.mcodelogic.safeareas.page;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.FlagValue;
import com.mcodelogic.safeareas.model.Region;
import com.mcodelogic.safeareas.model.enums.RegionFlag;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegionDetailPage extends InteractiveCustomUIPage<RegionDetailPage.RegionDetailEventData> {

    /** Sentinel: when in pendingChanges, means "clear flag to null" on save */
    private static final Object CLEAR_FLAG = new Object();

    private final Region region;
    private final Map<RegionFlag, Object> pendingChanges = new HashMap<>();
    private Integer pendingPriority = null;

    public static class RegionDetailEventData {
        public String action;
        public String flagName;
        public String flagValue;
        public String priorityValue;

        public static final BuilderCodec<RegionDetailEventData> CODEC = BuilderCodec.builder(RegionDetailEventData.class, RegionDetailEventData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (o, v) -> o.action = v, o -> o.action)
                .add()
                .append(new KeyedCodec<>("FlagName", Codec.STRING), (o, v) -> o.flagName = v, o -> o.flagName)
                .add()
                // NOTE: leading '@' is required for live UI value bindings
                .append(new KeyedCodec<>("@FlagValue", Codec.STRING), (o, v) -> o.flagValue = v, o -> o.flagValue)
                .add()
                .append(new KeyedCodec<>("@PriorityValue", Codec.STRING), (o, v) -> o.priorityValue = v, o -> o.priorityValue)
                .add()
                .build();
    }

    public RegionDetailPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl Region region) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, RegionDetailEventData.CODEC);
        this.region = region;
    }

    @Override
    public void build(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl UICommandBuilder commandBuilder,
            @NonNullDecl UIEventBuilder eventBuilder,
            @NonNullDecl Store<EntityStore> store
    ) {
        commandBuilder.append("Pages/RegionDetailPage.ui");

        commandBuilder.set("#RegionTitle.Text", region.getName().toUpperCase());
        commandBuilder.set("#RegionTypeLabel.Text", region.getType().name());
        commandBuilder.set("#PriorityInput.Value", String.valueOf(getEffectivePriority()));

        buildBooleanFlags(commandBuilder, eventBuilder);
        buildTextFlags(commandBuilder, eventBuilder);

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#PriorityInput",
                new EventData()
                        .append("Action", "SetPriority")
                        .append("@PriorityValue", "#PriorityInput.Value"),
                false
        );
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#PriorityDown",
                new EventData().append("Action", "DecPriority"),
                false
        );
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#PriorityUp",
                new EventData().append("Action", "IncPriority"),
                false
        );

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#BackButton",
                new EventData().append("Action", "Back")
        );

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SaveButton",
                new EventData().append("Action", "Save")
        );
    }

    private void buildBooleanFlags(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder) {
        commandBuilder.clear("#BooleanFlagsList");

        RegionFlag[] booleanFlags = Arrays.stream(RegionFlag.values()).filter(regionFlag -> regionFlag.getValueType().equals(Boolean.class)).toArray(RegionFlag[]::new);

        int i = 0;
        for (RegionFlag flag : booleanFlags) {
            String selector = "#BooleanFlagsList[" + i + "]";
            Boolean effective = effectiveBooleanValue(flag);

            commandBuilder.append("#BooleanFlagsList", "Pages/BooleanFlagRow.ui");
            commandBuilder.set(selector + " #FlagLabel.Text", formatFlagName(flag.name()));
            commandBuilder.set(selector + " #StateLabel.Text", effective == null ? "[ NULL ]" : (effective ? "[ ALLOW ]" : "[ DENY ]"));

            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector + " #BtnRemove",
                    new EventData().append("Action", "ClearFlag").append("FlagName", flag.name()), false);
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector + " #BtnAllow",
                    new EventData().append("Action", "SetFlagTrue").append("FlagName", flag.name()), false);
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector + " #BtnDeny",
                    new EventData().append("Action", "SetFlagFalse").append("FlagName", flag.name()), false);
            i++;
        }
    }

    /** null = not set, true = ON, false = OFF. Pending CLEAR_FLAG counts as null. */
    private Boolean effectiveBooleanValue(RegionFlag flag) {
        if (!pendingChanges.containsKey(flag)) {
            FlagValue<?> fv = region.getFlag(flag);
            return fv == null ? null : (Boolean) fv.getValue();
        }
        Object v = pendingChanges.get(flag);
        if (v == CLEAR_FLAG) return null;
        return (Boolean) v;
    }

    private void buildTextFlags(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder) {
        commandBuilder.clear("#TextFlagsList");

        RegionFlag[] textFlags = Arrays.stream(RegionFlag.values()).filter(regionFlag -> regionFlag.getValueType().equals(String.class)).toArray(RegionFlag[]::new);

        int i = 0;
        for (RegionFlag flag : textFlags) {
            String selector = "#TextFlagsList[" + i + "]";

            FlagValue<?> flagValue = region.getFlag(flag);
            String currentValue = pendingChanges.containsKey(flag)
                    ? (String) pendingChanges.get(flag)
                    : (flagValue != null ? (String) flagValue.getValue() : "");

            commandBuilder.append("#TextFlagsList", "Pages/TextFlagRow.ui");

            commandBuilder.set(selector + " #FlagLabel.Text", formatFlagName(flag.name()));
            commandBuilder.set(selector + " #Input.Value", currentValue);
            commandBuilder.set(selector + " #Input.PlaceholderText", RegionManager.instance.getLang().get("UiDetailPlaceholder", formatFlagName(flag.name()).toLowerCase()));

            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.ValueChanged,
                    selector + " #Input",
                    new EventData()
                            .append("Action", "UpdateTextFlag")
                            .append("FlagName", flag.name())
                            .append("@FlagValue", selector + " #Input.Value"),
                    false
            );
            i++;
        }
    }

    private int getEffectivePriority() {
        return pendingPriority != null ? pendingPriority : region.getPriority();
    }

    @Override
    public void handleDataEvent(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl RegionDetailEventData data
    ) {
        switch (data.action) {
            case "SetPriority":
                if (data.priorityValue != null) {
                    String v = data.priorityValue.trim();
                    if (v.isEmpty() || "-".equals(v)) {
                        pendingPriority = null;
                    } else {
                        try {
                            pendingPriority = Integer.parseInt(v);
                        } catch (NumberFormatException ignored) {
                            // keep current pending value
                        }
                    }
                }
                refreshDetailPage(ref, store);
                break;

            case "IncPriority":
                pendingPriority = getEffectivePriority() + 1;
                refreshDetailPage(ref, store);
                break;

            case "DecPriority":
                pendingPriority = getEffectivePriority() - 1;
                refreshDetailPage(ref, store);
                break;

            case "SetFlagTrue":
                if (data.flagName != null) {
                    RegionFlag flag = RegionFlag.valueOf(data.flagName);
                    pendingChanges.put(flag, true);
                    playerRef.sendMessage(RegionManager.instance.getLang().getMessage("UiDetailFlagAllow", formatFlagName(flag.name())));
                }
                refreshDetailPage(ref, store);
                break;

            case "SetFlagFalse":
                if (data.flagName != null) {
                    RegionFlag flag = RegionFlag.valueOf(data.flagName);
                    pendingChanges.put(flag, false);
                    playerRef.sendMessage(RegionManager.instance.getLang().getMessage("UiDetailFlagDeny", formatFlagName(flag.name())));
                }
                refreshDetailPage(ref, store);
                break;

            case "ClearFlag":
                if (data.flagName != null) {
                    RegionFlag flag = RegionFlag.valueOf(data.flagName);
                    pendingChanges.put(flag, CLEAR_FLAG);
                    playerRef.sendMessage(RegionManager.instance.getLang().getMessage("UiDetailFlagNull", formatFlagName(flag.name())));
                }
                refreshDetailPage(ref, store);
                break;

            case "UpdateTextFlag":
                if (data.flagName != null && data.flagValue != null) {
                    RegionFlag flag = RegionFlag.valueOf(data.flagName);
                    pendingChanges.put(flag, data.flagValue);
                }
                refreshDetailPage(ref, store);
                break;

            case "Save":
                savePendingChanges();
                playerRef.sendMessage(RegionManager.instance.getLang().getMessage("UiDetailSaved"));
                navigateBack(ref, store);
                break;

            case "Back":
                if (!pendingChanges.isEmpty()) {
                    playerRef.sendMessage(RegionManager.instance.getLang().getMessage("UiDetailDiscarded"));
                }
                pendingPriority = null;
                navigateBack(ref, store);
                break;

            default:
                sendUpdate();
                break;
        }
    }

    private void savePendingChanges() {
        if (pendingPriority != null) {
            region.setPriority(pendingPriority);
        }
        for (Map.Entry<RegionFlag, Object> entry : pendingChanges.entrySet()) {
            RegionFlag flag = entry.getKey();
            Object value = entry.getValue();

            if (flag.getValueType() == Boolean.class) {
                if (value == CLEAR_FLAG) {
                    region.getFlags().remove(flag);
                } else if (Boolean.TRUE.equals(value)) {
                    region.setFlag(flag, true);
                } else {
                    region.setFlag(flag, false);
                }
            } else if (flag.getValueType() == String.class) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    region.setFlag(flag, strValue.trim());
                } else {
                    // Remove flag if empty
                    region.getFlags().remove(flag);
                }
            }
        }
        
        RegionManager.instance.getApi().save(region);
        pendingChanges.clear();
        pendingPriority = null;
    }

    private void refreshDetailPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        UICommandBuilder cmd = new UICommandBuilder();
        UIEventBuilder ev = new UIEventBuilder();
        cmd.clear("#BooleanFlagsList");
        cmd.clear("#TextFlagsList");
        cmd.set("#PriorityInput.Value", String.valueOf(getEffectivePriority()));
        buildBooleanFlags(cmd, ev);
        buildTextFlags(cmd, ev);
        sendUpdate(cmd, ev, false);
    }

    private void navigateBack(Ref<EntityStore> ref, Store<EntityStore> store) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            RegionListPage listPage = new RegionListPage(playerRef);
            player.getPageManager().openCustomPage(ref, store, listPage);
        }
    }

    private String formatFlagName(String flagName) {
        return flagName.replace("_", " ");
    }
}
