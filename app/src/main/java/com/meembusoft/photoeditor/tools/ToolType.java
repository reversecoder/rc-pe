package com.meembusoft.photoeditor.tools;

public enum ToolType {

    BACKGROUND("Background"),
    CROP("Crop"),
    BRUSH("Brush"),
    TEXT("Text"),
    ERASER("Eraser"),
    FILTER("Filter"),
    SHADE("Shade"),
    WATERMARK("Watermark"),
    TRADEMARK("Trademark"),
    SEAL("Seal"),
    EMOJI("Emoji"),
    STICKER("Sticker");

    private String toolName;

    ToolType(String toolName) {
        this.toolName = toolName;
    }

    public String getToolName() {
        return this.toolName;
    }

    public static ToolType getToolType(String value) {
        for (ToolType toolType : ToolType.values()) {
            if (toolType.getToolName().equalsIgnoreCase(value)) {
                return toolType;
            }
        }
        return null;
    }
}