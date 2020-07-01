package com.codepath.android.lollipopexercise.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
//import android.os.Parcel;
//import org.parceler.Parcels;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
//import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.activities.DetailsActivity;
import com.codepath.android.lollipopexercise.models.Contact;

import java.util.List;

// Provide the underlying view for an individual list item.
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {
    private Activity mContext;
    private List<Contact> mContacts;
    private final Integer NUMBER_OF_COLORS = 24;

    public ContactsAdapter(Activity context, List<Contact> contacts) {
        mContext = context;
        if (contacts == null) {
            throw new IllegalArgumentException("contacts must not be null");
        }
        mContacts = contacts;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        Contact contact = mContacts.get(position);
        holder.rootView.setTag(contact);
        holder.tvName.setText(contact.getName());
        // Use Glide to get a callback with a Bitmap which can then
        // be used to extract a vibrant color from the Palette.
        // Define an asynchronous listener for image loading
        CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                // TODO 1. Instruct Glide to load the bitmap into the `holder.ivProfile` profile image view
                Glide.with(mContext).asBitmap().load(resource).into(holder.ivProfile);
                // TODO 2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                // This is the quick and easy integration path.
                // May not be optimal (since you're dipping in and out of threads)
                Palette.from(resource).maximumColorCount(NUMBER_OF_COLORS).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            // Set the background color of a layout based on the vibrant color
                            holder.vPalette.setBackgroundColor(vibrant.getRgb());
                            // Update the title TextView with the proper text color
                            holder.tvName.setTextColor(vibrant.getTitleTextColor());
                            //titleView.setTextColor(vibrant.getTitleTextColor());
                        }
                    }
                });
                // Set the result as the background color for `holder.vPalette` view containing the contact's name.
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // can leave empty
            }
        };

        // TODO: Clear the bitmap and the background color in adapter

        // Instruct Glide to load the bitmap into the asynchronous target defined above
        Glide.with(mContext).asBitmap().load(contact.getThumbnailDrawable()).centerCrop().into(target);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    // Provide a reference to the views for each contact item
    public class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);

            // Navigate to contact details activity on click of card view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Contact contact = (Contact)v.getTag();
                    if (contact != null) {
                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        // Fire an intent when a contact is selected
                        // Pass contact object in the bundle and populate details activity.
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("EXTRA_CONTACT", contact);
                        intent.putExtras(bundle);
                        Pair<View, String> p1 = Pair.create((View)ivProfile, "profile");
                        Pair<View, String> p2 = Pair.create(vPalette, "palette");
                        Pair<View, String> p3 = Pair.create((View)tvName, "text");
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(mContext, p1, p2, p3);
                        context.startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }
}
