package com.mcodelogic.safeareas.page;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.mcodelogic.safeareas.manager.RegionManager;
import com.mcodelogic.safeareas.model.Region;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class RegionListPage extends InteractiveCustomUIPage<RegionListPage.RegionListEventData> {

    /** null or "All" = show all worlds */
    private String selectedWorldFilter = null;
    /** Search query: filter regions by name or world (case-insensitive). */
    private String searchFilter = "";

    public static class RegionListEventData {
        public String action;
        public String regionId;
        public String worldFilter;
        public String searchQuery;

        public static final BuilderCodec<RegionListEventData> CODEC = BuilderCodec.builder(RegionListEventData.class, RegionListEventData::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (o, v) -> o.action = v, o -> o.action)
                .add()
                .append(new KeyedCodec<>("RegionId", Codec.STRING), (o, v) -> o.regionId = v, o -> o.regionId)
                .add()
                .append(new KeyedCodec<>("WorldFilter", Codec.STRING), (o, v) -> o.worldFilter = v, o -> o.worldFilter)
                .add()
                .append(new KeyedCodec<>("SearchQuery", Codec.STRING), (o, v) -> o.searchQuery = v, o -> o.searchQuery)
                .add()
                .build();
    }

    public RegionListPage(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, RegionListEventData.CODEC);
    }

    @Override
    public void build(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl UICommandBuilder commandBuilder,
            @NonNullDecl UIEventBuilder eventBuilder,
            @NonNullDecl Store<EntityStore> store
    ) {

        commandBuilder.append("Pages/RegionListPage.ui");

        List<Region> regions = getFilteredAndSortedRegions();
        commandBuilder.set("#RegionCount.Text", "REGIONS (" + regions.size() + ")");
        commandBuilder.set("#SearchInput.Value", searchFilter != null ? searchFilter : "");

        buildWorldFilterList(commandBuilder, eventBuilder);
        buildRegionList(commandBuilder, eventBuilder, regions);

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#SearchInput",
                new EventData()
                        .append("Action", "SearchFilter")
                        .append("SearchQuery", "#SearchInput.Value")
        );

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#RefreshButton",
                new EventData().append("Action", "Refresh")
        );

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseButton",
                new EventData().append("Action", "Close")
        );
    }

    private List<Region> getFilteredAndSortedRegions() {
        Collection<Region> all = RegionManager.instance.getApi().getAllRegions();
        String world = selectedWorldFilter;
        String search = (searchFilter != null) ? searchFilter.trim().toLowerCase() : "";

        return all.stream()
                .filter(r -> (world == null || world.isEmpty() || "All".equals(world) || r.getWorldName().equals(world)))
                .filter(r -> search.isEmpty()
                        || r.getName().toLowerCase().contains(search)
                        || (r.getWorldName() != null && r.getWorldName().toLowerCase().contains(search)))
                .sorted(Comparator.comparingInt(Region::getPriority).reversed())
                .toList();
    }

    private void buildWorldFilterList(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder) {
        commandBuilder.clear("#WorldFilterList");

        Collection<World> worlds = Universe.get().getWorlds().values();
        int idx = 0;
        commandBuilder.append("#WorldFilterList", "Pages/WorldFilterButton.ui");
        String allLabel = "All";
        commandBuilder.set("#WorldFilterList[0].Text", allLabel);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#WorldFilterList[0]",
                new EventData().append("Action", "WorldFilter").append("WorldFilter", allLabel), false);
        idx++;

        for (World w : worlds) {
            String name = w.getName();
            commandBuilder.append("#WorldFilterList", "Pages/WorldFilterButton.ui");
            commandBuilder.set("#WorldFilterList[" + idx + "].Text", name);
            final String worldName = name;
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#WorldFilterList[" + idx + "]",
                    new EventData().append("Action", "WorldFilter").append("WorldFilter", worldName), false);
            idx++;
        }
    }

    private void buildRegionList(UICommandBuilder commandBuilder, UIEventBuilder eventBuilder, List<Region> regions) {
        commandBuilder.clear("#RegionList");

        if (regions.isEmpty()) {
            commandBuilder.appendInline("#RegionList",
                    "Label { Text: \"No regions found\"; Anchor: (Height: 40); Style: (FontSize: 14, TextColor: #6e7da1); }"
            );
            return;
        }

        int i = 0;
        for (Region region : regions) {
            String selector = "#RegionList[" + i + "]";
            commandBuilder.append("#RegionList", "Pages/RegionEntry.ui");

            int flagCount = region.getFlags().size();
            String typeAndFlags = region.getType().name() + " · " + flagCount + " flag" + (flagCount == 1 ? "" : "s") + " set";

            commandBuilder.set(selector + " #RegionName.Text", region.getName());
            commandBuilder.set(selector + " #RegionTypeAndFlags.Text", typeAndFlags);
            commandBuilder.set(selector + " #RegionPriority.Text", String.valueOf(region.getPriority()));
            commandBuilder.set(selector + " #RegionWorld.Text", region.getWorldName());

            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    selector,
                    new EventData()
                            .append("Action", "SelectRegion")
                            .append("RegionId", region.getId().toString()),
                    false
            );
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    selector + " #DeleteBtn",
                    new EventData()
                            .append("Action", "DeleteRegion")
                            .append("RegionId", region.getId().toString()),
                    false
            );
            i++;
        }
    }

    @Override
    public void handleDataEvent(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl RegionListEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());

        switch (data.action) {
            case "SelectRegion":
                if (data.regionId != null) {
                    UUID regionId = UUID.fromString(data.regionId);
                    Region region = RegionManager.instance.getApi().getRegion(regionId);
                    if (region != null) {
                        RegionDetailPage detailPage = new RegionDetailPage(playerRef, region);
                        player.getPageManager().openCustomPage(ref, store, detailPage);
                    }
                }
                break;

            case "DeleteRegion":
                if (data.regionId != null) {
                    UUID regionId = UUID.fromString(data.regionId);
                    Region region = RegionManager.instance.getApi().getRegion(regionId);
                    if (region != null) {
                        RegionManager.instance.getApi().deleteRegion(regionId);
                        playerRef.sendMessage(Message.raw("§cRegion \"" + region.getName() + "\" deleted."));
                    }
                }
                refreshPage(ref, store);
                break;

            case "SearchFilter":
                searchFilter = (data.searchQuery != null) ? data.searchQuery : "";
                refreshPage(ref, store);
                break;

            case "WorldFilter":
                selectedWorldFilter = (data.worldFilter == null || "All".equals(data.worldFilter)) ? null : data.worldFilter;
                refreshPage(ref, store);
                break;

            case "Refresh":
                refreshPage(ref, store);
                break;

            case "Close":
                player.getPageManager().setPage(ref, store, Page.None);
                break;

            default:
                sendUpdate();
                break;
        }
    }

    private void refreshPage(Ref<EntityStore> ref, Store<EntityStore> store) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();

        List<Region> regions = getFilteredAndSortedRegions();
        commandBuilder.set("#RegionCount.Text", "REGIONS (" + regions.size() + ")");
        commandBuilder.set("#SearchInput.Value", searchFilter != null ? searchFilter : "");
        buildWorldFilterList(commandBuilder, eventBuilder);
        buildRegionList(commandBuilder, eventBuilder, regions);

        sendUpdate(commandBuilder, eventBuilder, false);
    }
}
