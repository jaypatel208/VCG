package dev.jay.visitingcardgenerator.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.razorpay.PaymentResultListener;

import dev.jay.visitingcardgenerator.databinding.ActivityMainBinding;
import dev.jay.visitingcardgenerator.fragment.PreviewFragment;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private PreviewFragment getPreviewFragment() {
        NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment();
        if (navFragment != null) {
            FragmentManager fragmentManager = navFragment.getChildFragmentManager();
            Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
            if (fragment instanceof PreviewFragment && fragment.isVisible()) {
                return (PreviewFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        PreviewFragment previewFragment = getPreviewFragment();
        if (previewFragment != null) {
            previewFragment.handlePaymentSuccess();
        } else {
            Toast.makeText(this, "Payment successful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        PreviewFragment previewFragment = getPreviewFragment();
        if (previewFragment != null) {
            previewFragment.handlePaymentError(response);
        } else {
            Toast.makeText(this, "Payment failed: " + response, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}