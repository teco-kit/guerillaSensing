package com.teco.vindi.guerillasensingui3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        this.mDeviceTypeList = contactList;
    }

    // Returns number of items in list.
    @Override
    public int getItemCount() {
        return mDeviceTypeList.size();
    }

    // Returns the type of the view at the given position.
    @Override
    public int getItemViewType(int position) {

    }

    // Called when data of item at 'position' is to be displayed using the given viewholder.
    @Override
    public void onBindViewHolder(ViewHolder deviceTypeViewHolder, int position) {
        MainContent2.DeviceType ci = mDeviceTypeList.get(i);
        deviceTypeViewHolder.mNameView.setText(ci.name);
        deviceTypeViewHolder.vSurname.setText(ci.surname);
        deviceTypeViewHolder.vEmail.setText(ci.email);
        deviceTypeViewHolder.vTitle.setText(ci.name + " " + ci.surname);
    }

    // Called when a new item for the recycler view is created.
    // Inflate the right layout, depending on the given item type.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_card, viewGroup, false);
        return new ViewHolder(itemView, );
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
        protected TextView mHeadLine;
        protected TextView mSubLine;

        // For the "items".
        protected TextView mNameView;
        protected TextView mVersionView;
        protected TextView mCreationDateView;
        protected TextView mPictureView;

        // For the "separators".
        protected TextView mSeparator;

        /**
         * Take the inflated view v and get references to all relevant fields.
         * @param v The inflated view.
         * @param type The type of the view. Depending on this, the needed references will differ.
         */
        public ViewHolder(View view, int type) {
            super(v);

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

        }

        private void setReferencesForItem(View view) {
            this.mNameView =  (TextView) view.findViewById(R.id.txtName);
            this.mVersionView =  (TextView) view.findViewById(R.id.txtName);
            this.mCreationDateView =  (TextView) view.findViewById(R.id.txtName);
            this.mPictureView =  (TextView) view.findViewById(R.id.txtName);
        }

        private void setReferencesForSeparator(View view) {
            
        }

        private void handleUnknownType(View view) {

        }

        public int getType() {
            return TYPE;
        }
    }
}