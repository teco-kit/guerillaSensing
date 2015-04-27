package edu.teco.guerillaSensing;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    // When inflating the items, we have to distinguish between the regular items and the header.
    // This could be expanded to support more item types, a footer, etc.
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private String name;        //String Resource for header View Name
    private int profile;        //int Resource for header view profile picture
    private String email;       //String Resource for header view email


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;
        TextView Name;
        TextView email;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {


                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }


    }


    DrawerAdapter(String Titles[], int Icons[], String Name, String Email) { // MyAdapter Constructor with titles and icons parameter
        // titles, icons, name, email, profile pic are passed from the main activity as we
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        name = Name;
        email = Email;


    }

    private ObjectAnimator animation1;
    private ObjectAnimator animation2;
    public void animategroup1(int x, int y, Object val, int valx, int valy ){

        System.out.println("animategroup1 entered here");
        System.out.println("x:"+x);
        System.out.println("y"+y);
        System.out.println("valx"+valx);
        System.out.println("valy"+valy);

        animation1 = ObjectAnimator.ofFloat(val, "x", valx, x);

        animation2 = ObjectAnimator.ofFloat(val, "y", valy, y);

        AnimatorSet set = new AnimatorSet();


        animation1.setDuration(3000);
        animation1.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) ((Math.sin(v * 2 * Math.PI - Math.PI / 2) + 1) / 2);
            }
        });

        animation2.setDuration(849);
        animation2.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) ((Math.sin(v * 2 * Math.PI - Math.PI / 2) + 1) / 2);
            }
        });

        animation1.setRepeatMode(ValueAnimator.RESTART);
        animation1.setRepeatCount(ValueAnimator.INFINITE);
        animation2.setRepeatMode(ValueAnimator.RESTART);
        animation2.setRepeatCount(ValueAnimator.INFINITE);

        set.playTogether(animation1, animation2);

        set.start();

    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            final View img = v.findViewById(R.id.name);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animategroup1((int) img.getX() + 460, (int) img.getY() + 30, img, (int) img.getX(), (int) img.getY());
                }
            }, 100);

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageView.setImageResource(mIcons[position - 1]);// Settimg the image with array of our icons
        } else {

            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void changeAt(int position) {
        mNavTitles[position - 1] = "ddd";
        notifyItemChanged(position);
    }

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