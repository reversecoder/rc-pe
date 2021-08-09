package com.meembusoft.photoeditor.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meembusoft.photoeditor.R;

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
        mToolList.add(new ToolModel(ToolType.BACKGROUND, R.drawable.ic_background));
        mToolList.add(new ToolModel(ToolType.CROP, R.drawable.ic_crop));
        mToolList.add(new ToolModel(ToolType.BRUSH, R.drawable.ic_brush));
        mToolList.add(new ToolModel(ToolType.TEXT, R.drawable.ic_text));
        mToolList.add(new ToolModel(ToolType.ERASER, R.drawable.ic_eraser));
        mToolList.add(new ToolModel(ToolType.FILTER, R.drawable.ic_photo_filter));
        mToolList.add(new ToolModel(ToolType.SHADE, R.drawable.ic_shade));
        mToolList.add(new ToolModel(ToolType.WATERMARK, R.drawable.ic_watermark));
        mToolList.add(new ToolModel(ToolType.TRADEMARK, R.drawable.ic_trademark_round));
        mToolList.add(new ToolModel(ToolType.SEAL, R.drawable.ic_seal));
        mToolList.add(new ToolModel(ToolType.EMOJI, R.drawable.ic_insert_emoticon));
        mToolList.add(new ToolModel(ToolType.STICKER, R.drawable.ic_sticker));
    }

    public void setPickerType(PickerType mPickerType) {
        this.mPickerType = mPickerType;
        ToolModel toolBackground = getTool(ToolType.BACKGROUND);
        ToolModel toolCrop = getTool(ToolType.CROP);
        if (mPickerType == PickerType.CANVAS) {
            if (toolCrop != null) {
                int cropToolPosition = getToolPosition(ToolType.CROP);
                if (cropToolPosition != -1) {
                    mToolList.remove(cropToolPosition);
                }
            }
            if (toolBackground == null) {
                mToolList.add(0, new ToolModel(ToolType.BACKGROUND, R.drawable.ic_background));
            }
        } else {
            if (toolBackground != null) {
                int backgroundToolPosition = getToolPosition(ToolType.BACKGROUND);
                if (backgroundToolPosition != -1) {
                    mToolList.remove(backgroundToolPosition);
                }
            }
            if (toolCrop == null) {
                mToolList.add(0, new ToolModel(ToolType.CROP, R.drawable.ic_crop));
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

        ToolModel(ToolType toolType, int toolIcon) {
            mToolName = toolType.getToolName();
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
