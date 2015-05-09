package edu.teco.guerillaSensing.adapters;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.teco.guerillaSensing.R;

/**
 * Adapter for the navigation drawer
 * The view holder is also included in this class as a static nested class.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    // When inflating the items, we have to distinguish between the regular items and the header.
    // This could be expanded to support more item types, a footer, etc.
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // String array to store the passed titles.
    private String mNavTitles[];

    // Integer array to store the passed icon resources.
    private int mIcons[];

    // Headline, shown in the header.
    private String mHeadline;

    // Subline, shown in the header.
    private String mSubline;

    /**
     * The ViewHolder which extends the RecyclerView View Holder.
     * ViewHolders are used to to store the inflated views in order to recycle them so we don't have
     * to inflate views multiple times.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // The view holder type. Can be TYPE_HEADER (=0) or TYPE_ITEM (=1).
        int mHolderType;

        // The views for headers. They will be inflated.
        TextView mHeadLineView;
        TextView mSubLineView;

        // The views for menu items. They will be inflated.
        ImageView mIconView;
        TextView mMenuEntryView;


        /**
         * Constructor for the ViewHolder.
         * @param itemView The inflated view.
         * @param viewType The type of the view.
         */
        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            // Set the references to the subviews, depending on the type.
            if (viewType == TYPE_ITEM) {
                mMenuEntryView = (TextView) itemView.findViewById(R.id.rowText);
                mIconView = (ImageView) itemView.findViewById(R.id.rowIcon);
                mHolderType = TYPE_ITEM;
            } else if (viewType == TYPE_HEADER) {
                mHeadLineView = (TextView) itemView.findViewById(R.id.drawer_header_headline);
                mSubLineView = (TextView) itemView.findViewById(R.id.drawer_header_subline);
                mHolderType = TYPE_HEADER;
            }
        }
    }


    /**
     * Constructor for the {@link DrawerAdapter} which holds the data for the navigation drawer.
     * @param navTitles The menu entries.
     * @param icons The menu icons.
     * @param headLine The header headline.
     * @param subLine The header subline.
     */
    public DrawerAdapter(String navTitles[], int icons[], String headLine, String subLine) {
        mNavTitles = navTitles;
        mIcons = icons;
        mHeadline = headLine;
        mSubline = subLine;
    }

    // Called when the RecyclerView needs a new ViewHolder of the given type.
    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            // Inflate type ITEM.
            View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false);

            // Put it in ViewHolder together with its type and return it.
            return new ViewHolder(inflated, viewType);

        } else if (viewType == TYPE_HEADER) {
            // Inflate type ITEM.
            View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);

            // Put it in ViewHolder together with its type and return it.
            return new ViewHolder(inflated, viewType);
        }

        // Unknown type. This should not happen.
        return null;
    }


    // Called when the item at the given position needs to be displayed.
    // To display it, we're given a ViewHolde. Now we just need to set the contents.
    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        // Set contents depending on type.
        if (holder.mHolderType == TYPE_ITEM) {
            // Subtract one for header.
            holder.mMenuEntryView.setText(mNavTitles[position - 1]);
            holder.mIconView.setImageResource(mIcons[position - 1]);
        } else if (holder.mHolderType == TYPE_HEADER) {
            holder.mHeadLineView.setText(mHeadline);
            holder.mSubLineView.setText(mSubline);
        }
    }

    // Returns the number of items in the list.
    @Override
    public int getItemCount() {
        // Add one for the header.
        return mNavTitles.length + 1;
    }


    // Returns the type of the item at the given position.
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    /**
     * Returns true if the item at the given position is the header.
     * @param position The position of the item to check.
     * @return True if the item is the header, false otherwise.
     */
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    /**
     * Removes item at given position from the menu.
     * @param position The position of the item to remove.
     */
    public void removeAt(int position) {
        int n = 0;
        String[] newNavTitles = new String[mNavTitles.length - 1];
        int[] newIcons = new int[mIcons.length - 1];

        for (int i = 0; i < mNavTitles.length; i++) {
            if (position - 1 != i) {
                newNavTitles[n] = mNavTitles[i];
                n++;
            }
        }

        n = 0;
        for (int i = 0; i < mIcons.length; i++) {
            if (position - 1 != i) {
                newIcons[n] = mIcons[i];
                n++;
            }
        }

        mNavTitles = newNavTitles.clone();
        mIcons = newIcons.clone();

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mNavTitles.length - 1);
    }
}