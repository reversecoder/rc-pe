package com.meembusoft.photoeditor.tools;

public class ToolModel {

    private String mToolName;
    private int mToolIcon;
    private ToolType mToolType;

    public ToolModel(ToolType toolType, int toolIcon) {
        mToolName = toolType.getToolName();
        mToolIcon = toolIcon;
        mToolType = toolType;
    }

    public String getToolName() {
        return mToolName;
    }

    public void setToolName(String mToolName) {
        this.mToolName = mToolName;
    }

    public int getToolIcon() {
        return mToolIcon;
    }

    public void setToolIcon(int mToolIcon) {
        this.mToolIcon = mToolIcon;
    }

    public ToolType getToolType() {
        return mToolType;
    }

    public void setToolType(ToolType mToolType) {
        this.mToolType = mToolType;
    }

    public ToolModel setToolModel(ToolModel toolModel) {
        mToolName = toolModel.mToolType.getToolName();
        mToolIcon = toolModel.mToolIcon;
        mToolType = toolModel.mToolType;
        return this;
    }
}