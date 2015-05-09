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

    // List of menu items.
    private List<CardMenuEntry> mMenuItemList;

    /**
     * Constructor for the {@link edu.teco.guerillaSensing.adapters.MenuCardAdapter}. Creates an adapter
     * for the given list of card entries.
     * @param deviceTypeList
     */
    public MenuCardAdapter(List<CardMenuEntry> deviceTypeList) {
        this.mMenuItemList = deviceTypeList;
    }

    /**
     * Adds an item to the list and updates the view.
     * @param newItem The new item.
     */
    public void addItem(CardMenuEntry newItem) {
        this.mMenuItemList.add(newItem);

        // Item is added at the end of the list. Notify adapter.
        this.notifyItemInserted(mMenuItemList.size() - 1);
    }

    /**
     * Notifies the adapter that an item has changed at the given positon.
     * @param position The position of the item that was changed.
     */
    public void hasChanged(int position) {
        this.notifyItemChanged(position);
    }

    // Returns number of items in list.
    @Override
    public int getItemCount() {
        return mMenuItemList.size();
    }

    // Get the type of a view at a given position.
    // Default implementation always returns 0, assuming all items have the same layout.
    @Override
    public int getItemViewType(int position) {
        return mMenuItemList.get(position).getType();
    }

    // Called when data of item at 'position' is to be displayed using the given viewholder.
    // Depending on the type, different fields (references) are available.
    @Override
    public void onBindViewHolder(ViewHolder menuEntryViewHolder, int position) {
        // Get the item at the given position.
        CardMenuEntry item = mMenuItemList.get(position);

        // Check the item type. Is could be a header, item or footer.
        // Then, set the fields depending on the type.
        switch (item.getType()) {
            case MenuRecyclerTypes.TYPE_HEADER:
                menuEntryViewHolder.mHeaderImageView.setImageResource(item.getHeaderImage());
                menuEntryViewHolder.mHeadLineView.setText(item.getHeadLine());
                menuEntryViewHolder.mSubLineView.setText(item.getSubLine());
                break;
            case MenuRecyclerTypes.TYPE_ITEM:
                menuEntryViewHolder.mPictureView.setImageResource(item.getPicture());
                menuEntryViewHolder.mNameView.setText(item.getFirstLine());
                menuEntryViewHolder.mVersionView.setText(item.getSecondLine());
                menuEntryViewHolder.mCreationDateView.setText(item.getThirdLine());
                break;
            case MenuRecyclerTypes.TYPE_FOOTER:
                menuEntryViewHolder.mSeparatorView.setText(item.getFooterLine());
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
                    this.mHeaderImageView = (ImageView) view.findViewById(R.id.device_select_header_image);
                    this.mHeadLineView = (TextView) view.findViewById(R.id.device_select_header_headline);
                    this.mSubLineView = (TextView) view.findViewById(R.id.device_select_header_subline);
                    break;
                case MenuRecyclerTypes.TYPE_ITEM:
                    this.mPictureView = (ImageView) view.findViewById(R.id.device_select_item_picture);
                    this.mNameView = (TextView) view.findViewById(R.id.device_select_item_name);
                    this.mVersionView = (TextView) view.findViewById(R.id.device_select_item_version);
                    this.mCreationDateView = (TextView) view.findViewById(R.id.device_select_item_date);
                    break;
                case MenuRecyclerTypes.TYPE_FOOTER:
                    this.mSeparatorView = (TextView) view.findViewById(R.id.device_select_separator_text);
                    break;
                default:
                    break;

            }

        }

        public int getType() {
            return TYPE;
        }
    }
}