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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;

import dev.jay.visitingcardgenerator.R;
import dev.jay.visitingcardgenerator.databinding.FragmentSignUpBinding;
import dev.jay.visitingcardgenerator.utils.PreferenceManager;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(requireContext());

        binding.btnSignUp.setOnClickListener(v -> performSignUp());

        return binding.getRoot();
    }

    private void performSignUp() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();

        if (!isValidEmail(email)) {
            binding.emailInputLayout.setError("Please enter a valid email.");
            return;
        } else {
            binding.emailInputLayout.setError(null);
        }

        if (!isValidPassword(password)) {
            binding.passwordInputLayout.setError("Password must be at least 6 characters.");
            return;
        } else {
            binding.passwordInputLayout.setError(null);
        }

        binding.tvErrorMessage.setVisibility(View.GONE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        preferenceManager.saveLoginStatus(true);

                        Toast.makeText(getContext(), "Sign-up successful!", Toast.LENGTH_SHORT).show();

                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.loginSignUpFragment, true)
                                .build();

                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_signUpFragment_to_homeFragment, null, navOptions);
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            binding.tvErrorMessage.setText("This email is already registered.");
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            binding.tvErrorMessage.setText("Invalid email or password format.");
                        } else {
                            binding.tvErrorMessage.setText("Something went wrong, please try again.");
                        }
                        binding.tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
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