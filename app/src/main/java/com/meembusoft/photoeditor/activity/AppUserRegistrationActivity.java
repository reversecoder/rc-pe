package com.meembusoft.photoeditor.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.base.BaseActivity;
import com.meembusoft.photoeditor.util.AppUtil;
import com.meembusoft.photoeditor.util.KeyboardManager;
import com.meembusoft.photoeditor.util.Logger;
import com.meembusoft.photoeditor.util.ValidationManager;

import me.jerryhanks.countrypicker.PhoneNumberEditTextWithMask;

public class AppUserRegistrationActivity extends BaseActivity {

    private String TAG = AppUserRegistrationActivity.class.getSimpleName();
    private Button btnRegisterUser;
    private ImageView ivAcceptedPhone, ivAcceptedEmail;
    private PhoneNumberEditTextWithMask edtPhone;
    private EditText edtName, edtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_app_user_registration);

        btnRegisterUser = (Button) findViewById(R.id.btn_register_user);
        ivAcceptedPhone = (ImageView) findViewById(R.id.iv_accepted_phone);
        ivAcceptedEmail = (ImageView) findViewById(R.id.iv_accepted_email);
        edtPhone = (PhoneNumberEditTextWithMask) findViewById(R.id.edt_phone);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);

        //For disabling country picker
        edtPhone.setPickerEnable(false);

        if (AppUtil.isDebug(AppUserRegistrationActivity.this)) {
            edtPhone.setText("1794-620787");
            edtEmail.setText("rashed.droid@gmail.com");
            edtName.setText("Md. Rashadul Alam");

            validateMobileNumber();
            validateEmail();
        }

        edtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateMobileNumber();
            }
        });

        edtPhone.setOnTextChangeListener(new PhoneNumberEditTextWithMask.onTextChangeListener() {
            @Override
            public void onTextChange(CharSequence s, int start, int before, int count) {
                validateMobileNumber();
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateEmail();
            }
        });

        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide keyboard
                KeyboardManager.hideKeyboard(AppUserRegistrationActivity.this);

                //Check empty fields
                //Check phone number
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Toast.makeText(AppUserRegistrationActivity.this, getString(R.string.toast_please_input_your_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }
                final String phoneNumber = edtPhone.getFullNumberWithPlus();
                Logger.d(TAG, TAG + " >>> " + "phoneNumberVerification(setOnClickListener): " + phoneNumber);
                if (!ValidationManager.isValidBangladeshiMobileNumber(phoneNumber)) {
                    Toast.makeText(AppUserRegistrationActivity.this, getString(R.string.toast_please_input_valid_mobile_no), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check name
                final String name = edtName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AppUserRegistrationActivity.this, getString(R.string.toast_please_input_your_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check email
                final String email = edtEmail.getText().toString();
                if (!TextUtils.isEmpty(email)) {
                    if (!ValidationManager.isValidEmail(email)) {
                        Toast.makeText(AppUserRegistrationActivity.this, getString(R.string.toast_please_input_valid_email), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Check internet connection
//                if (!NetworkManager.isConnected(AppUserRegistrationActivity.this)) {
//                    Toast.makeText(AppUserRegistrationActivity.this, getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
//                    return;
//                }
            }
        });
    }

    /**************
     * Validation *
     **************/
    private void validateMobileNumber() {
        if (ValidationManager.isValidBangladeshiMobileNumber(edtPhone.getFullNumberWithPlus())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedPhone.setVisibility(View.VISIBLE);
            ivAcceptedPhone.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedPhone.setVisibility(View.GONE);
        }
    }

    private void validateEmail() {
        if (ValidationManager.isValidEmail(edtEmail.getText().toString())) {
            Logger.d(TAG, "validation: valid");
            ivAcceptedEmail.setVisibility(View.VISIBLE);
            ivAcceptedEmail.setBackgroundResource(R.drawable.vector_accepted);
        } else {
            Logger.d(TAG, "validation: gone invalid");
            ivAcceptedEmail.setVisibility(View.GONE);
        }
    }

}
