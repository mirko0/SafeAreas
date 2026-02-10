package com.mcodelogic.safeareas.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

/**
 * Configuration for all user-facing messages. Users can edit the language config file
 * to customize every message shown by the plugin (notifications, commands, UI).
 * <p>
 * Config keys must be PascalCase (e.g. ProtectionBuildPrimary) as expected by the config format.
 * <p>
 * Placeholders in messages use {0}, {1}, {2}, etc. and are replaced when using
 * {@link Lang#get(String, Object...)}. Messages can use TinyMsg tags for styling, e.g.
 * <code>&lt;red&gt;Error!&lt;/red&gt;</code>, <code>&lt;gray&gt;Hint.&lt;/gray&gt;</code>.
 */
@Getter
public class LangConfig {

    public static final BuilderCodec<LangConfig> CODEC = ConfigCodecBuilder.create(LangConfig.class, LangConfig::new);

    // ----- Protection notifications (primary = title, secondary = subtitle) -----
    @ConfigField("ProtectionBuildPrimary")
    private String protectionBuildPrimary = "<red><bold>Block Placing!</bold></red>";
    @ConfigField("ProtectionBuildSecondary")
    private String protectionBuildSecondary = "<gray>You cannot place blocks in this region!</gray>";

    @ConfigField("ProtectionBreakPrimary")
    private String protectionBreakPrimary = "<red><bold>Block Breaking Disabled!</bold></red>";
    @ConfigField("ProtectionBreakSecondary")
    private String protectionBreakSecondary = "<gray>You cannot break blocks in this region!</gray>";

    @ConfigField("ProtectionCraftPrimary")
    private String protectionCraftPrimary = "<red><bold>Crafting Disabled!</bold></red>";
    @ConfigField("ProtectionCraftSecondary")
    private String protectionCraftSecondary = "<gray>You cannot craft in this region!</gray>";

    @ConfigField("ProtectionDropPrimary")
    private String protectionDropPrimary = "<red><bold>Item Drops Disabled!</bold></red>";
    @ConfigField("ProtectionDropSecondary")
    private String protectionDropSecondary = "<gray>You cannot drop items in this region!</gray>";

    @ConfigField("ProtectionInteractPrimary")
    private String protectionInteractPrimary = "<red><bold>Interaction Disabled!</bold></red>";
    @ConfigField("ProtectionInteractSecondary")
    private String protectionInteractSecondary = "<gray>You cannot interact in this region!</gray>";

    @ConfigField("ProtectionBlockDamagePrimary")
    private String protectionBlockDamagePrimary = "<red><bold>Block Damage Disabled!</bold></red>";
    @ConfigField("ProtectionBlockDamageSecondary")
    private String protectionBlockDamageSecondary = "<gray>You cannot damage blocks in this region!</gray>";

    // ----- Command messages -----
    @ConfigField("CommandRegionNotFound")
    private String commandRegionNotFound = "<red>Region not found!</red>";

    @ConfigField("CommandRegionDeleted")
    private String commandRegionDeleted = "<green>Region deleted: {0}</green>";
    @ConfigField("CommandRegionRenamed")
    private String commandRegionRenamed = "<green>Region {0} renamed to: {1}.</green>";
    @ConfigField("CommandRegionFlagInvalid")
    private String commandRegionFlagInvalid = "<red>Invalid flag! Available Flags: {0}</red>";
    @ConfigField("CommandRegionFlagRemoved")
    private String commandRegionFlagRemoved = "<green>Flag {0} removed from region {1}</green>";
    @ConfigField("CommandRegionFlagSet")
    private String commandRegionFlagSet = "<green>Flag {0} set to {1} for region {2}</green>";
    @ConfigField("CommandRegionPrioritySet")
    private String commandRegionPrioritySet = "<green>Priority set to {0} for region {1}</green>";
    @ConfigField("CommandRegionCreateInvalidType")
    private String commandRegionCreateInvalidType = "<red>Invalid region type! Available Types: GLOBAL, AREA, CUBOID</red>";
    @ConfigField("CommandRegionCreateNameExists")
    private String commandRegionCreateNameExists = "<red>Region with name {0} already exists!</red>";
    @ConfigField("CommandRegionCreateGlobalExists")
    private String commandRegionCreateGlobalExists = "<red>Global region already exists!</red>";
    @ConfigField("CommandRegionCreateUseSelectionTool")
    private String commandRegionCreateUseSelectionTool = "<red><bold>Use the Selection Tool to select a region!</bold></red>";
    @ConfigField("CommandRegionCreateError")
    private String commandRegionCreateError = "<red>Error happened while creating region! Send logs to author!</red>";
    @ConfigField("CommandRegionCreateSuccessHeader")
    private String commandRegionCreateSuccessHeader = "<green><bold>——— Region Created ———</bold></green>\n";
    @ConfigField("CommandRegionCreateSuccessName")
    private String commandRegionCreateSuccessName = "<gray>Name:</gray> <white>{0}</white>\n";
    @ConfigField("CommandRegionCreateSuccessType")
    private String commandRegionCreateSuccessType = "<gray>Type:</gray> <white>{0}</white>\n";
    @ConfigField("CommandRegionCreateSuccessBounds")
    private String commandRegionCreateSuccessBounds = "<gray>Bounds:</gray> <white>{0} — {1}</white>\n";
    @ConfigField("CommandRegionCreateSuccessBoundsGlobal")
    private String commandRegionCreateSuccessBoundsGlobal = "<gray>Bounds:</gray> <white>Full world</white>\n";
    @ConfigField("CommandRegionCreateArgType")
    private String commandRegionCreateArgType = "Region type: GLOBAL, AREA, or CUBOID";
    @ConfigField("CommandRegionCreateArgName")
    private String commandRegionCreateArgName = "Unique name for the region";
    @ConfigField("CommandRegionListHeader")
    private String commandRegionListHeader = "<gray>Regions ({0}): {1}</gray>";
    @ConfigField("CommandRegionFlagsAvailable")
    private String commandRegionFlagsAvailable = "<green>Available flags: \n</green>{0}";
    @ConfigField("CommandRegionInfoNotFound")
    private String commandRegionInfoNotFound = "<red>Region not found!</red>";
    @ConfigField("CommandRegionInfoHeader")
    private String commandRegionInfoHeader = "<dark_gray>======== <gold><bold>Region: {0}</bold></gold> ========</dark_gray>\n";
    @ConfigField("CommandRegionInfoName")
    private String commandRegionInfoName = "<gold>Name:</gold> <gray>{0}</gray>\n";
    @ConfigField("CommandRegionInfoWorld")
    private String commandRegionInfoWorld = "<gold>World:</gold> <gray>{0}</gray>\n";
    @ConfigField("CommandRegionInfoPriority")
    private String commandRegionInfoPriority = "<gold>Priority:</gold> <gray>{0}</gray>\n";
    @ConfigField("CommandRegionInfoType")
    private String commandRegionInfoType = "<gold>Type:</gold> <gray>{0}</gray>\n";
    @ConfigField("CommandRegionInfoArea")
    private String commandRegionInfoArea = "<gold>Area:</gold> <gray>{0} — {1}</gray>\n";
    @ConfigField("CommandRegionInfoFlags")
    private String commandRegionInfoFlags = "<gold>Flags:</gold> <gray>{0}</gray>\n";
    @ConfigField("CommandRegionInfoNoFlags")
    private String commandRegionInfoNoFlags = "<gold>Flags:</gold> <dark_gray>none</dark_gray>\n";

    // ----- UI messages -----
    @ConfigField("UiRegionsCount")
    private String uiRegionsCount = "REGIONS ({0})";
    @ConfigField("UiRegionsEmpty")
    private String uiRegionsEmpty = "No regions found";
    @ConfigField("UiRegionDeleted")
    private String uiRegionDeleted = "<red>Region \"{0}\" deleted.</red>";
    @ConfigField("UiDetailFlagAllow")
    private String uiDetailFlagAllow = "<yellow>{0}</yellow> -> <green>ALLOW</green>";
    @ConfigField("UiDetailFlagDeny")
    private String uiDetailFlagDeny = "<yellow>{0}</yellow> -> <red>DENY</red>";
    @ConfigField("UiDetailFlagNull")
    private String uiDetailFlagNull = "<gray>{0}</gray> -> <dark_gray>NULL</dark_gray>";
    @ConfigField("UiDetailSaved")
    private String uiDetailSaved = "<green>Region flags saved!</green>";
    @ConfigField("UiDetailDiscarded")
    private String uiDetailDiscarded = "<gold>Unsaved changes discarded</gold>";
    @ConfigField("UiDetailPlaceholder")
    private String uiDetailPlaceholder = "Enter {0}...";

    // ----- Greeting / farewell -----
    @ConfigField("GreetingDefaultSubtitle")
    private String greetingDefaultSubtitle = "SafeAreas";

    // ----- Command descriptions (plain text for /help) -----
    @ConfigField("CommandDescRegion")
    private String commandDescRegion = "Manage servers regions";
    @ConfigField("CommandDescRegionUi")
    private String commandDescRegionUi = "Open the region management UI interface";
    @ConfigField("CommandDescRegionDelete")
    private String commandDescRegionDelete = "Delete selected region.";
    @ConfigField("CommandDescRegionRename")
    private String commandDescRegionRename = "Rename existing region.";
    @ConfigField("CommandDescRegionFlag")
    private String commandDescRegionFlag = "Add flag to region.";
    @ConfigField("CommandDescRegionRemoveflag")
    private String commandDescRegionRemoveflag = "Remove a flag from region.";
    @ConfigField("CommandDescRegionPriority")
    private String commandDescRegionPriority = "Set region priority.";
    @ConfigField("CommandDescRegionCreate")
    private String commandDescRegionCreate = "Create a new region.";
    @ConfigField("CommandDescRegionList")
    private String commandDescRegionList = "List regions in a world.";
    @ConfigField("CommandDescRegionFlags")
    private String commandDescRegionFlags = "List available flags.";
    @ConfigField("CommandDescRegionInfo")
    private String commandDescRegionInfo = "Show region info.";
    @ConfigField("CommandDescTest")
    private String commandDescTest = "Open region management UI.";

    public LangConfig() {
    }
}
