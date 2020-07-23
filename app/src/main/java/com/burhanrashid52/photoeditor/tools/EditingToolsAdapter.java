package com.burhanrashid52.photoeditor.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.burhanrashid52.photoeditor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();
    private OnItemSelected mOnItemSelected;
    private PickerType mPickerType;

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
        mToolList.add(new ToolModel("Background", R.drawable.ic_background, ToolType.BACKGROUND));
        mToolList.add(new ToolModel("Crop", R.drawable.ic_crop, ToolType.CROP));
        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));
    }

    public void setPickerType(PickerType mPickerType) {
        this.mPickerType = mPickerType;
        ToolModel toolModel = getTool(ToolType.BACKGROUND);
        if (mPickerType == PickerType.CANVAS) {
            if (toolModel == null) {
                mToolList.add(0, new ToolModel("Background", R.drawable.ic_background, ToolType.BACKGROUND));
            }
        } else {
            if (toolModel != null) {
                int toolPosition = getToolPosition(ToolType.BACKGROUND);
                if (toolPosition != -1) {
                    mToolList.remove(toolPosition);
                }
            }
        }
        notifyDataSetChanged();
    }

    private ToolModel getTool(ToolType toolType) {
        if (mToolList.size() > 0) {
            for (ToolModel toolModel : mToolList) {
                if (toolModel.mToolType == toolType) {
                    return toolModel;
                }
            }
        }
        return null;
    }

    private int getToolPosition(ToolType toolType) {
        if (mToolList.size() > 0) {
            for (int i = 0; i < mToolList.size(); i++) {
                if (mToolList.get(i).mToolType == toolType) {
                    return i;
                }
            }
        }
        return -1;
    }

    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    class ToolModel {
        private String mToolName;
        private int mToolIcon;
        private ToolType mToolType;

        ToolModel(String toolName, int toolIcon, ToolType toolType) {
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
                }
            });
        }
    }
}
