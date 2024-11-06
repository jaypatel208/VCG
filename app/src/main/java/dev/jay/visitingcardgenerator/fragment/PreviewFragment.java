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

import dev.jay.visitingcardgenerator.databinding.FragmentPreviewBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PreviewFragment extends Fragment {

    private FragmentPreviewBinding binding;
    private Bitmap cardBitmap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPreviewBinding.inflate(inflater, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name");
            String designation = args.getString("designation");
            String mobile = "Mobile Number: " + args.getString("mobile");
            String email = "Email: " + args.getString("email");
            String companyName = "Company Name: " + args.getString("companyName");

            binding.tvName.setText(name);
            binding.tvDesignation.setText(designation);
            binding.tvMobile.setText(mobile);
            binding.tvEmail.setText(email);
            binding.tvCompanyName.setText(companyName);
        }

        binding.btnShare.setOnClickListener(v -> {
            View cardView = binding.cardVisitingCard;
            cardBitmap = getBitmapFromView(cardView);
            shareCardImage(cardBitmap);
        });

        return binding.getRoot();
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    private void shareCardImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            getContext();
            OutputStream outputStream = requireContext().openFileOutput("visiting_card.jpg", Context.MODE_PRIVATE);
            outputStream.write(byteArray);
            outputStream.close();

            File file = new File(requireContext().getFilesDir(), "visiting_card.jpg");
            Uri uri = FileProvider.getUriForFile(requireContext(), "dev.jay.visitingcardgenerator.fileprovider", file);

            android.content.Intent shareIntent = new android.content.Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my visiting card!");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // Grant read permission to the app

            startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to share the image.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}