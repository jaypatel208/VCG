package dev.jay.visitingcardgenerator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

import dev.jay.visitingcardgenerator.R;
import dev.jay.visitingcardgenerator.databinding.FragmentLoginSignUpBinding;
import dev.jay.visitingcardgenerator.utils.PreferenceManager;

public class LoginSignUpFragment extends Fragment {

    private FragmentLoginSignUpBinding binding;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginSignUpBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(requireContext());

        if (preferenceManager.isLoggedIn()) {
            NavHostFragment.findNavController(this).navigate(R.id.action_loginSignUpFragment_to_homeFragment);
            return binding.getRoot();
        }

        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.btnSignUp.setOnClickListener(v -> navigateToSignUp());

        return binding.getRoot();
    }

    private void performLogin() {
        String email = Objects.requireNonNull(binding.edtUserName.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();

        if (!isValidEmail(email)) {
            binding.usernameInputLayout.setError("Please enter a valid email.");
            return;
        } else {
            binding.usernameInputLayout.setError(null);
        }

        if (!isValidPassword(password)) {
            binding.passwordInputLayout.setError("Password must be at least 6 characters.");
            return;
        } else {
            binding.passwordInputLayout.setError(null);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                        preferenceManager.saveLoginStatus(true);

                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.loginSignUpFragment, true)
                                .build();

                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_loginSignUpFragment_to_homeFragment, null, navOptions);
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            binding.tvErrorMessage.setText("No user found with this email.");
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            binding.tvErrorMessage.setText("Invalid password.");
                        } else {
                            binding.tvErrorMessage.setText("Something went wrong, please try again.");
                        }
                        binding.tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void navigateToSignUp() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_loginSignUpFragment_to_signUpFragment);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
