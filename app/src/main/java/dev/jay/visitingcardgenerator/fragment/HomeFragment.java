package dev.jay.visitingcardgenerator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import java.util.Objects;

import dev.jay.visitingcardgenerator.R;
import dev.jay.visitingcardgenerator.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                navigateToPreviewFragment();
            }
        });

        return binding.getRoot();
    }

    private boolean validateForm() {
        String name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
        String designation = Objects.requireNonNull(binding.edtDesignation.getText()).toString().trim();
        String mobile = Objects.requireNonNull(binding.edtMobile.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String companyName = Objects.requireNonNull(binding.edtCompanyName.getText()).toString().trim();

        boolean isValid = true;

        if (name.isEmpty()) {
            binding.nameInputLayout.setError("Name cannot be empty.");
            isValid = false;
        } else {
            binding.nameInputLayout.setError(null);
        }

        if (designation.isEmpty()) {
            binding.designationInputLayout.setError("Designation cannot be empty.");
            isValid = false;
        } else {
            binding.designationInputLayout.setError(null);
        }

        if (mobile.length() != 10) {
            binding.mobileInputLayout.setError("Please enter a valid 10-digit mobile number.");
            isValid = false;
        } else {
            binding.mobileInputLayout.setError(null);
        }

        if (!email.contains("@")) {
            binding.emailInputLayout.setError("Please enter a valid email.");
            isValid = false;
        } else {
            binding.emailInputLayout.setError(null);
        }

        if (companyName.isEmpty()) {
            binding.companyInputLayout.setError("Company name cannot be empty.");
            isValid = false;
        } else {
            binding.companyInputLayout.setError(null);
        }

        return isValid;
    }

    private void navigateToPreviewFragment() {
        String name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
        String designation = Objects.requireNonNull(binding.edtDesignation.getText()).toString().trim();
        String mobile = Objects.requireNonNull(binding.edtMobile.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String companyName = Objects.requireNonNull(binding.edtCompanyName.getText()).toString().trim();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("designation", designation);
        bundle.putString("mobile", mobile);
        bundle.putString("email", email);
        bundle.putString("companyName", companyName);

        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build();


        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_homeFragment_to_previewFragment, bundle, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
