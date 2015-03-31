package com.teco.vindi.guerillasensingui3;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for the card view.
 * The view holder is also included in this class as a static nested class.
 */
public class MenuCardAdapter extends RecyclerView.Adapter<MenuCardAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_SEPARATOR = 2;

    private List<MainContent2.DeviceType> mDeviceTypeList;

    public MenuCardAdapter(List<MainContent2.DeviceType> deviceTypeList) {
        this.mDeviceTypeList = deviceTypeList;
    }

    // Returns number of items in list.
    @Override
    public int getItemCount() {
        return mDeviceTypeList.size();
    }

    // Get the type of a view at a given position.
    // Default implementation always returns 0, assuming all items have the same layout.
    @Override
    public int getItemViewType(int position) {
        return mDeviceTypeList.get(position).getType();
    }

    // Called when data of item at 'position' is to be displayed using the given viewholder.
    // Depending on the type, different fields (references) are available.
    @Override
    public void onBindViewHolder(ViewHolder deviceTypeViewHolder, int position) {
        MainContent2.DeviceType item = mDeviceTypeList.get(position);

        switch (item.getType()) {
            case TYPE_HEADER:
                deviceTypeViewHolder.mHeaderImageView.setImageResource(item.mHeaderImage);
                deviceTypeViewHolder.mHeadLineView.setText(item.mHeadLine);
                deviceTypeViewHolder.mSubLineView.setText(item.mSubLine);
                break;
            case TYPE_ITEM:
                deviceTypeViewHolder.mPictureView.setImageResource(item.mPicture);
                deviceTypeViewHolder.mNameView.setText(item.mName);
                deviceTypeViewHolder.mVersionView.setText(item.mVersion);
                deviceTypeViewHolder.mCreationDateView.setText(item.mCreationDate);
                break;
            case TYPE_SEPARATOR:
                deviceTypeViewHolder.mSeparatorView.setText(item.mSeparator);
                break;
            default:
                break;
        }

    }

    // Called when a new item for the recycler view is created.
    // Inflate the right layout, depending on the given item type.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View itemView;

        switch (type) {
            case TYPE_HEADER:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_select_header, viewGroup, false);
                break;
            case TYPE_ITEM:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_select, viewGroup, false);
                break;
            case TYPE_SEPARATOR:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_select_separator, viewGroup, false);
                break;
            default:
                itemView = null;
                break;
        }

        return new ViewHolder(itemView, type);
    }

    /**
     * The view holder for the header, items and separators.
     * Putting all of them in one class seems to be some kind of anti-pattern.
     * TODO: Find a better way to do this.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // The data type (header, item or separator).
        private static int TYPE;

        // For the "headers".
        protected ImageView mHeaderImageView;
        protected TextView mHeadLineView;
        protected TextView mSubLineView;

        // For the "items".
        protected ImageView mPictureView;
        protected TextView mNameView;
        protected TextView mVersionView;
        protected TextView mCreationDateView;

        // For the "separators".
        protected TextView mSeparatorView;

        /**
         * Take the inflated view v and get references to all relevant fields.
         * @param view The inflated view.
         * @param type The type of the view. Depending on this, the needed references will differ.
         */
        public ViewHolder(View view, int type) {
            super(view);

            // Set type.
            this.TYPE = type;

            // Set references depending on type.
            switch (type) {
                case TYPE_HEADER:
                    setReferencesForHeader(view);
                    break;
                case TYPE_ITEM:
                    setReferencesForItem(view);
                    break;
                case TYPE_SEPARATOR:
                    setReferencesForSeparator(view);
                    break;
                default:
                    handleUnknownType(view);
                    break;

            }

        }


        private void setReferencesForHeader(View view) {
            this.mHeaderImageView = (ImageView) view.findViewById(R.id.device_select_header_image);
            this.mHeadLineView = (TextView) view.findViewById(R.id.device_select_header_headline);
            this.mSubLineView = (TextView) view.findViewById(R.id.device_select_header_subline);
        }

        private void setReferencesForItem(View view) {
            this.mPictureView = (ImageView) view.findViewById(R.id.device_select_item_picture);
            this.mNameView = (TextView) view.findViewById(R.id.device_select_item_name);
            this.mVersionView = (TextView) view.findViewById(R.id.device_select_item_version);
            this.mCreationDateView = (TextView) view.findViewById(R.id.device_select_item_date);
        }

        private void setReferencesForSeparator(View view) {
            this.mSeparatorView = (TextView) view.findViewById(R.id.device_select_separator_text);
        }

        private void handleUnknownType(View view) {
            // TODO: Handle somehow.
        }

        public int getType() {
            return TYPE;
        }
    }
}