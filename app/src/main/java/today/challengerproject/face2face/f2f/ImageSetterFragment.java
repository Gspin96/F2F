package today.challengerproject.face2face.f2f;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

import static today.challengerproject.face2face.f2f.HelperMethods.dipToPixels;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //ImageSetterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageSetterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageSetterFragment extends Fragment {

    private Intent data;
    private Context context;
    private ImageSetterFragment fragment;

    /*private OnFragmentInteractionListener mListener;*/

    public ImageSetterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param data the image data.
     * @return A new instance of fragment ImageSetterFragment.
     */
    public static ImageSetterFragment newInstance(Intent data, Context context) {
        ImageSetterFragment fragment = new ImageSetterFragment();

        fragment.setData(data);
        fragment.setContext(context);

        return fragment;
    }

    public void setData(Intent data) {
        this.data = data;
    }
    public Intent getData() {
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_image_setter, container, false);

        this.fragment = this;

        try {
            Uri uri = data.getData();

            int height_dp = 256;

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            int width = (int) (dipToPixels(context, height_dp) / bitmap.getHeight() * bitmap.getWidth());

            bitmap = Bitmap.createScaledBitmap(bitmap, width, (int) dipToPixels(context, height_dp), false);

            ImageView image = view.findViewById(R.id.imageView);

            image.setImageBitmap(bitmap);

            AppCompatImageButton clearButton = view.findViewById(R.id.clearButton);

            clearButton.setOnClickListener(new AppCompatImageButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        /*mListener = null;*/
    }

    public void setContext(Context context) {
        this.context = context;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
