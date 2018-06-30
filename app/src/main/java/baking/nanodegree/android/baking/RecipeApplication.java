package baking.nanodegree.android.baking;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

import java.util.ArrayList;

public class RecipeApplication extends Application {

    public void onCreate() {
        super.onCreate();
        final Context context = this;

        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(new SampleBumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    private static class SampleBumperPluginsProvider implements DumperPluginsProvider {
        private final Context mContext;

        private SampleBumperPluginsProvider(Context context) {this.mContext = context; }

        @Override
        public Iterable<DumperPlugin> get() {
            ArrayList<DumperPlugin> plugins = new ArrayList<>();
            for(DumperPlugin dumperPlugin : Stetho.defaultDumperPluginsProvider(mContext).get()){
                plugins.add(dumperPlugin);
            }
            return plugins;
        }
    }
}
