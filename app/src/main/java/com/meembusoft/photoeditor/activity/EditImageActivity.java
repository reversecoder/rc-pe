package com.meembusoft.photoeditor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.meembusoft.photoeditor.BubbleFlag;
import com.meembusoft.photoeditor.EmojiBSFragment;
import com.meembusoft.photoeditor.PropertiesBSFragment;
import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.StickerBSFragment;
import com.meembusoft.photoeditor.TextEditorDialogFragment;
import com.meembusoft.photoeditor.base.BaseActivity;
import com.meembusoft.photoeditor.filters.FilterListener;
import com.meembusoft.photoeditor.filters.FilterViewAdapter;
import com.meembusoft.photoeditor.tools.EditingToolsAdapter;
import com.meembusoft.photoeditor.tools.PickerType;
import com.meembusoft.photoeditor.tools.ToolType;
import com.meembusoft.photoeditor.util.AppUtil;
import com.meembusoft.photoeditor.util.BuilderManager;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ymex.popup.dialog.PopupDialog;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.enumeration.PhotoFilter;
import ja.burhanrashid52.photoeditor.enumeration.ViewType;
import ja.burhanrashid52.photoeditor.listener.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.util.BitmapManager;
import ja.burhanrashid52.photoeditor.util.SaveSettings;
import ja.burhanrashid52.photoeditor.util.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.view.PhotoEditorView;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public static final String FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoeditor.fileprovider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    public static final String IMAGE_URI = "IMAGE_URI";
    PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    private PickerType mPickerType;
    private Uri imageUri;
    private CropIwaResultReceiver cropResultReceiver;

    @Nullable
    @VisibleForTesting
    Uri mSaveImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);

        initViews();
        initializeBmb2();

        handleIntentImage(mPhotoEditorView.getSource());

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .setWatermark(getString(R.string.text_default_water_mark))
                .setSeal(getString(R.string.text_default_seal))
                .setShadeColor(838860800)
                .build(); // build photo editor sdk
        // Shadow color
        //838860800//50
        //335544320//20
        //167772160//10

        mPhotoEditor.setOnPhotoEditorListener(this);

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
    }

    private void handleIntentImage(ImageView source) {
        Intent intent = getIntent();
        if (intent != null) {
            String intentType = intent.getType();
            if (intentType != null && intentType.startsWith("image/")) {
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    source.setImageURI(imageUri);
                }
            }
        }
    }

    private void initViews() {
        ImageView imgCanvas;
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;
        ImageView imgShare;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCanvas = findViewById(R.id.imgCanvas);
        imgCanvas.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgShare = findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);

        cropResultReceiver = new CropIwaResultReceiver();
        cropResultReceiver.setListener(new CropIwaResultReceiver.Listener() {
            @Override
            public void onCropSuccess(Uri croppedUri) {
                Log.d(TAG, "imageUri>>onCropSuccess>>croppedUri: " + croppedUri.toString());
                try {
                    imageUri = croppedUri;
                    mPhotoEditor.clearAllViews(true);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    mPickerType = PickerType.GALLERY;
                    mEditingToolsAdapter.setPickerType(mPickerType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCropFailed(Throwable e) {

            }
        });
        cropResultReceiver.register(EditImageActivity.this);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgUndo:
                if (!isImageAvailable()) {
                    Toast.makeText(EditImageActivity.this, "Please pick image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                if (!isImageAvailable()) {
                    Toast.makeText(EditImageActivity.this, "Please pick image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                if (!isImageAvailable()) {
                    Toast.makeText(EditImageActivity.this, "Please pick image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgShare:
                if (!isImageAvailable()) {
                    Toast.makeText(EditImageActivity.this, "Please pick image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                shareImage();
                break;

            case R.id.imgCanvas:
                showCreateCanvasDialog();
                break;

            case R.id.imgCamera:
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                dispatchTakePictureIntent();
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }

    private void shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share));
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri));
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)));
    }

    private Uri buildFileProviderUri(@NonNull Uri uri) {
        return FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                new File(uri.getPath()));
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...");

            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy, EEEE, hh:mm:ss a");
            String dateTime = simpleDateFormat.format(now);

            // Create folder if not exist
            String rootPath = Environment.getExternalStorageDirectory() + File.separator + AppUtil.getApplicationName(EditImageActivity.this);
            File folder = new File(rootPath);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // image naming and path  to include sd card  appending name you choose for file
            String filePath = rootPath + File.separator + AppUtil.getApplicationName(EditImageActivity.this) + "_" + dateTime + ".png";

            File file = new File(filePath);
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        imageUri = mSaveImageUri = Uri.fromFile(new File(imagePath));
                        mPhotoEditorView.getSource().setImageURI(mSaveImageUri);

                        // Insert to the gallery
                        insertImageToGallery(imagePath);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    try {
                        Log.d(TAG, "onActivityResult>>CAMERA_REQUEST>>imageUri: " + imageUri);
                        mPhotoEditor.clearAllViews(true);
                        Bitmap cameraBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        mPhotoEditorView.getSource().setImageBitmap(cameraBitmap);
                        mPickerType = PickerType.CAMERA;
                        mEditingToolsAdapter.setPickerType(mPickerType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case PICK_REQUEST:
                    try {
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult>>PICK_REQUEST>>imageUri: " + imageUri);
                        mPhotoEditor.clearAllViews(true);
                        Bitmap galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        mPhotoEditorView.getSource().setImageBitmap(galleryBitmap);
                        mPickerType = PickerType.GALLERY;
                        mEditingToolsAdapter.setPickerType(mPickerType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        if (!isImageAvailable()) {
            Toast.makeText(EditImageActivity.this, "Please pick image first", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (toolType) {
            case BACKGROUND:
                openBackgroundSelectorDialog();
                mTxtCurrentTool.setText(R.string.label_background);
                break;
            case CROP:
//                if (!mPhotoEditor.isCacheEmpty()) {
//                    Toast.makeText(EditImageActivity.this, "Please save image before cropping", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                mTxtCurrentTool.setText(R.string.label_crop);
                Intent intentCrop = new Intent(EditImageActivity.this, CropActivity.class);
                intentCrop.putExtra(IMAGE_URI, imageUri);
                startActivity(intentCrop);
                break;
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser_mode);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case SHADE:
                openShadeSelectorDialog();
                mTxtCurrentTool.setText(R.string.label_shade);
                break;
            case WATERMARK:
                Toast.makeText(EditImageActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
//                mTxtCurrentTool.setText(R.string.label_watermark);
            case SEAL:
                Toast.makeText(EditImageActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
//                mTxtCurrentTool.setText(R.string.label_seal);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
        }
    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void openBackgroundSelectorDialog() {
        ColorPickerDialog.Builder builder =
                new ColorPickerDialog.Builder(this, R.style.DarkDialog)
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("Test")
                        .setPositiveButton(
                                getString(R.string.confirm),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        Toast.makeText(EditImageActivity.this, "#" + envelope.getHexCode(), Toast.LENGTH_SHORT).show();
                                        mPhotoEditorView.colorizeImage(envelope.getColor());
                                    }
                                })
                        .setNegativeButton(
                                getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
        ColorPickerView colorPickerView = builder.getColorPickerView();
        colorPickerView.setFlagView(new BubbleFlag(this, R.layout.layout_flag));
        builder.show();
    }

    private void openShadeSelectorDialog() {
        ColorPickerDialog.Builder builder =
                new ColorPickerDialog.Builder(this, R.style.DarkDialog)
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("Test")
                        .setPositiveButton(
                                getString(R.string.confirm),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        Toast.makeText(EditImageActivity.this, "#" + envelope.getHexCode(), Toast.LENGTH_SHORT).show();
                                        mPhotoEditor.applyShade(envelope.getColor());
                                    }
                                })
                        .setNegativeButton(
                                getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
        ColorPickerView colorPickerView = builder.getColorPickerView();
        colorPickerView.setFlagView(new BubbleFlag(this, R.layout.layout_flag));
        builder.show();
    }

    private boolean isImageAvailable() {
        if (mPickerType == null) {
            return false;
        }
        return true;
    }

    private void showCreateCanvasDialog() {
        PopupDialog.create(this)
                .view(R.layout.dialog_new_canvas, new PopupDialog.OnBindViewListener() {
                    @Override
                    public void onCreated(final PopupDialog dialog, final View layout) {
                        layout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        layout.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText edtWidth = layout.findViewById(R.id.edt_width);
                                EditText edtHeight = layout.findViewById(R.id.edt_height);

                                if (edtWidth.getText().toString().length() <= 0) {
                                    Toast.makeText(EditImageActivity.this, "Please input width", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (edtHeight.getText().toString().length() <= 0) {
                                    Toast.makeText(EditImageActivity.this, "Please input height", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                int width = 0, height = 0;
                                width = Integer.parseInt(edtWidth.getText().toString());
                                height = Integer.parseInt(edtHeight.getText().toString());

                                mPhotoEditor.clearAllViews(true);
                                Bitmap bitmap = BitmapManager.createImage(width, height, Color.WHITE);
                                mPhotoEditorView.getSource().setImageBitmap(bitmap);
                                mPickerType = PickerType.CANVAS;
                                mEditingToolsAdapter.setPickerType(mPickerType);

                                dialog.dismiss();
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cropResultReceiver.unregister(EditImageActivity.this);
    }

    /*
     * Camera
     * */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(EditImageActivity.this,
                            EditImageActivity.this.getPackageName() + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
        }
    }

    private void insertImageToGallery(String currentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void initializeBmb2() {
        BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.bmb_menu);
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++)
            bmb.addBuilder(BuilderManager.getSimpleCircleButtonBuilder());

        bmb.getCustomButtonPlacePositions().add(new PointF(Util.dp2px(-80), Util.dp2px(-80)));
        bmb.getCustomButtonPlacePositions().add(new PointF(0, 0));
        bmb.getCustomButtonPlacePositions().add(new PointF(Util.dp2px(+80), Util.dp2px(+80)));
    }
}