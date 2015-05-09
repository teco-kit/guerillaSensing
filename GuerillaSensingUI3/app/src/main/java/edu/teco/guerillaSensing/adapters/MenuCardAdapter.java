package edu.teco.guerillaSensing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.teco.guerillaSensing.R;
import edu.teco.guerillaSensing.data.CardMenuEntry;
import edu.teco.guerillaSensing.data.MenuRecyclerTypes;

/**
 * Adapter for the card view.
 * The view holder is also included in this class as a static nested class.
 */
public class MenuCardAdapter extends RecyclerView.Adapter<MenuCardAdapter.ViewHolder> {

    private List<CardMenuEntry> mDeviceTypeList;

    public MenuCardAdapter(List<CardMenuEntry> deviceTypeList) {
        this.mDeviceTypeList = deviceTypeList;
    }

    // Adds item to list.
    public void addItem(CardMenuEntry newItem) {
        this.mDeviceTypeList.add(newItem);
        this.notifyItemInserted(mDeviceTypeList.size() - 1);
    }

    // Notification that item has changed
    public void hasChanged(int position) {
        this.notifyItemChanged(position);
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
        CardMenuEntry item = mDeviceTypeList.get(position);

        switch (item.getType()) {
            case MenuRecyclerTypes.TYPE_HEADER:
                deviceTypeViewHolder.mHeaderImageView.setImageResource(item.getHeaderImage());
                deviceTypeViewHolder.mHeadLineView.setText(item.getHeadLine());
                deviceTypeViewHolder.mSubLineView.setText(item.getSubLine());
                break;
            case MenuRecyclerTypes.TYPE_ITEM:
                deviceTypeViewHolder.mPictureView.setImageResource(item.getPicture());
                deviceTypeViewHolder.mNameView.setText(item.getFirstLine());
                deviceTypeViewHolder.mVersionView.setText(item.getSecondLine());
                deviceTypeViewHolder.mCreationDateView.setText(item.getThirdLine());
                break;
            case MenuRecyclerTypes.TYPE_FOOTER:
                deviceTypeViewHolder.mSeparatorView.setText(item.getFooterLine());
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
            case MenuRecyclerTypes.TYPE_HEADER:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_select_header, viewGroup, false);
                break;
            case MenuRecyclerTypes.TYPE_ITEM:
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_select, viewGroup, false);
                break;
            case MenuRecyclerTypes.TYPE_FOOTER:
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
                case MenuRecyclerTypes.TYPE_HEADER:
                    setReferencesForHeader(view);
                    break;
                case MenuRecyclerTypes.TYPE_ITEM:
                    setReferencesForItem(view);
                    break;
                case MenuRecyclerTypes.TYPE_FOOTER:
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
            // Shouldn't happen.
        }

        public int getType() {
            return TYPE;
        }
    }
}