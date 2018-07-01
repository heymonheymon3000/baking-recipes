package baking.nanodegree.android.baking.ui.recipe;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import android.net.Uri;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.util.HashMap;

public class VideoRequestHandler extends RequestHandler {
    public static String SCHEME_VIDEO="videoframe";

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));
    }

    @Override
    public Result load(Request data, int arg1)  {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(Uri.parse("https:"+data.uri.getPath()).toString(),
                new HashMap<String, String>());
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(0);
        return new Result(bitmap, Picasso.LoadedFrom.NETWORK);
    }
}
