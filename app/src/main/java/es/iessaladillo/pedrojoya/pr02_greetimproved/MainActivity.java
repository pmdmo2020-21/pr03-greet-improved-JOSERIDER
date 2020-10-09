package es.iessaladillo.pedrojoya.pr02_greetimproved;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import es.iessaladillo.pedrojoya.pr02_greetimproved.databinding.MainActivityBinding;
import es.iessaladillo.pedrojoya.pr02_greetimproved.utils.SoftInputUtils;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private int countGreet = 0;
    private boolean isPremium = false;

    private RadioButton currentTreatment;
    private TextWatcher edtNameTextWatcher;
    private TextWatcher edtSirnameTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.edtName.setOnFocusChangeListener((v, isFocussed) -> setCharsLeftColor(isFocussed, binding.lblCharsLeftName));
        binding.edtSirname.setOnFocusChangeListener((v, isFocussed) -> setCharsLeftColor(isFocussed, binding.lblCharsLeftSirname));
        binding.switchPremium.setOnCheckedChangeListener((compoundButton, b) -> setProgressVisibility(b));
        binding.rdgTreatment.setOnCheckedChangeListener((radioGroup, i) -> setTreatment(radioGroup));
        addTextWatcherListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeListenersAssociation();
    }

    private void setupViews() {
        setupListeners();
        setDefaults();
        showProgress();
    }

    private void setupListeners() {
        binding.btnGreet.setOnClickListener(v -> greet());
        binding.edtSirname.setOnEditorActionListener((v, actionId, event) -> {
            greet();
            return true;
        });
    }

    private void addTextWatcherListeners() {
        edtNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateAndShowCharsLeft(editable.toString(), binding.edtName, binding.lblCharsLeftName);
            }
        };
        edtSirnameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateAndShowCharsLeft(editable.toString(), binding.edtSirname, binding.lblCharsLeftSirname);
            }
        };

        binding.edtName.addTextChangedListener(edtNameTextWatcher);
        binding.edtSirname.addTextChangedListener(edtSirnameTextWatcher);
    }

    private void validateAndShowCharsLeft(String str, EditText edtText, TextView lbl) {
        if (isNotBlankOrEmpty(str)) {
            edtText.setError(null);
            int charactersLeft = getResources().getInteger(R.integer.edt_maxLines) - str.length();
            lbl.setText(getResources().getQuantityString(R.plurals.characters_left, charactersLeft, charactersLeft));
        } else {
            edtText.setError(getString(R.string.edt_error_required));
        }
    }

    private void removeListenersAssociation() {
        binding.edtName.setOnFocusChangeListener(null);
        binding.edtSirname.setOnFocusChangeListener(null);

        binding.rdgTreatment.setOnCheckedChangeListener(null);
        binding.switchPremium.setOnCheckedChangeListener(null);
        binding.edtName.removeTextChangedListener(edtNameTextWatcher);
        binding.edtSirname.removeTextChangedListener(edtSirnameTextWatcher);
    }

    private void setDefaults() {
        int charactersLef = getResources().getInteger(R.integer.edt_maxLines);
        binding.lblCharsLeftName.setText(getResources().getQuantityString(R.plurals.characters_left, charactersLef, charactersLef));
        binding.lblCharsLeftSirname.setText(getResources().getQuantityString(R.plurals.characters_left, charactersLef, charactersLef));
        setCharsLeftColor(true, binding.lblCharsLeftName);
        setTreatment(binding.rdgTreatment);
    }

    private void showProgress() {
        binding.prBarGreet.setProgress(countGreet);
        binding.lblCountGreet.setText(getString(R.string.lbl_count_greet, countGreet, getResources().getInteger(R.integer.max_greets)));
    }

    private void setProgressVisibility(boolean isChecked) {
        countGreet = 0;
        isPremium = isChecked;
        binding.prBarGreet.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        binding.lblCountGreet.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        if (!isChecked) showProgress();
    }

    private void setTreatment(RadioGroup radioGroup) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rdb_mr:
                setImgTreatment(R.drawable.ic_mr);
                break;
            case R.id.rdb_mrs:
                setImgTreatment(R.drawable.ic_mrs);
                break;
            case R.id.rdb_ms:
                setImgTreatment(R.drawable.ic_ms);
                break;
            default:
                throw new RuntimeException("RadioButton id not found");
        }
        currentTreatment = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
    }

    private void setImgTreatment(int drawable) {
        binding.imgTreatment.setImageResource(drawable);
    }

    private void greet() {
        String name = binding.edtName.getText().toString();
        String sirname = binding.edtSirname.getText().toString();

        if (isValidForm(name, sirname)) {
            int maxGreets = getResources().getInteger(R.integer.max_greets);
            SoftInputUtils.hideSoftKeyboard(binding.edtSirname);

            if (countGreet >= maxGreets) {
                showMessage(getString(R.string.message_buy_premium));
                return;
            }

            if (!isPremium) {
                countGreet++;
                showProgress();
            }
            showGreet(name, sirname);
        } else {
            showErrors();
        }
    }

    private boolean isValidForm(String name, String sirname) {
        return isNotBlankOrEmpty(name) && isNotBlankOrEmpty(sirname);
    }

    private void showGreet(String name, String sirname) {
        String treatment = currentTreatment.getText().toString();
        showMessage(binding.chkPolitely.isChecked() ? getString(R.string.lbl_greet_politely, treatment, name, sirname) : getString(R.string.lbl_greet_no_politely, name, sirname));
    }

    private void showErrors() {
        if (isBlankOrEmpty(binding.edtName.getText().toString())) {
            binding.edtName.setError(getString(R.string.edt_error_required));
            binding.edtName.requestFocus();
        } else if (isBlankOrEmpty(binding.edtSirname.getText().toString())) {
            binding.edtSirname.setError(getString(R.string.edt_error_required));
            binding.edtSirname.requestFocus();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setCharsLeftColor(boolean isFocussed, TextView lbl) {
        lbl.setTextColor(ContextCompat.getColor(this, isFocussed ? R.color.colorAccent : R.color.textPrimary));
    }

    /**
     * Check if the string is valid ( it is not empty and it does not start with blank spaces and it does not end with blank spaces).
     *
     * @param str string to check if it is valid.
     * @return true if the string is valid or false if the string is not valid.
     */
    private boolean isNotBlankOrEmpty(String str) {
        return !isBlankOrEmpty(str);
    }

    /**
     * Check if the string is valid ( it is empty and it start with blank spaces and it end with blank spaces).
     *
     * @param str string to check if it is valid.
     * @return true if the string is valid or false if the string is not valid.
     */
    private boolean isBlankOrEmpty(String str) {
        return str.isEmpty() || str.startsWith(" ") || str.endsWith(" ");
    }

}