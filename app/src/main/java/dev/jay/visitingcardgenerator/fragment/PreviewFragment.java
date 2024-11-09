package dev.jay.visitingcardgenerator.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.razorpay.Checkout;

import dev.jay.visitingcardgenerator.databinding.FragmentPreviewBinding;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PreviewFragment extends Fragment {

    private FragmentPreviewBinding binding;
    private static final String FILE_NAME = "visiting_card.jpg";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPreviewBinding.inflate(inflater, container, false);

        setCardDetails();
        binding.btnShare.setOnClickListener(v -> startPayment());

        return binding.getRoot();
    }

    private void setCardDetails() {
        Bundle args = getArguments();
        if (args != null) {
            binding.tvName.setText(args.getString("name"));
            binding.tvDesignation.setText(args.getString("designation"));
            binding.tvMobile.setText("Mobile Number: " + args.getString("mobile"));
            binding.tvEmail.setText("Email: " + args.getString("email"));
            binding.tvCompanyName.setText("Company Name: " + args.getString("companyName"));
        }
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_FuJB7pytdobJhY");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Visiting Card Payment");
            options.put("description", "Payment for sharing visiting card");
            options.put("currency", "INR");
            options.put("amount", "10000"); // Amount in paise
            checkout.open(getActivity(), options);
        } catch (Exception e) {
            showToast(e.getLocalizedMessage());
        }
    }

    public void handlePaymentSuccess() {
        Bitmap cardBitmap = getBitmapFromView(binding.cardVisitingCard);
        shareCardImage(cardBitmap);
    }

    public void handlePaymentError(String response) {
        showToast(response);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private void shareCardImage(Bitmap bitmap) {
        try {
            Uri uri = saveImageToFile(bitmap);
            if (uri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my visiting card!");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, "Share via"));
            } else {
                showToast("Failed to share the image.");
            }
        } catch (IOException e) {
            showToast("Failed to share the image." + e.getLocalizedMessage());
        }
    }

    private Uri saveImageToFile(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        try (OutputStream outputStream = requireContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            outputStream.write(byteArrayOutputStream.toByteArray());
        }

        File file = new File(requireContext().getFilesDir(), FILE_NAME);
        return FileProvider.getUriForFile(requireContext(), "dev.jay.visitingcardgenerator.fileprovider", file);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}