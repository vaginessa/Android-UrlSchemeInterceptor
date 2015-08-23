package fr.smarquis.urlscheme;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class UrlSchemeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        Intent intent = getIntent();
        fillIntent(intent, ((TextView) findViewById(R.id.intent)));
        fillData(intent, ((TextView) findViewById(R.id.data)));
        fillExtras(intent, ((TextView) findViewById(R.id.extras)));
    }

    private void fillIntent(@Nullable Intent intent, @NonNull TextView textView) {
        if (intent == null) {
            textView.setText(Printer.EMPTY);
            return;
        }
        Truss truss = new Truss();
        Printer.append(truss, "action", intent.getAction());
        Printer.append(truss, "categories", intent.getCategories());
        Printer.append(truss, "type", intent.getType());
        Printer.append(truss, "flags", Flags.decode(intent.getFlags()));
        Printer.append(truss, "package", intent.getPackage());
        Printer.append(truss, "component", intent.getComponent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Printer.append(truss, "referrer", getReferrer());
        }
        textView.setText(truss.build());
    }

    private void fillData(@Nullable Intent intent, @NonNull TextView textView) {
        Uri data = intent == null ? null : intent.getData();
        if (data == null) {
            textView.setText(Printer.EMPTY);
            return;
        }

        Truss truss = new Truss();
        Printer.append(truss, "raw", data);
        Printer.append(truss, "scheme", data.getScheme());
        Printer.append(truss, "host", data.getHost());
        Printer.append(truss, "port", data.getPort());
        Printer.append(truss, "path", data.getPath());
        Printer.appendKey(truss, "query");
        if (data.isHierarchical()) {
            for (String queryParameterName : data.getQueryParameterNames()) {
                Printer.appendSecondary(truss, queryParameterName, data.getQueryParameter(queryParameterName));
            }
        }
        Printer.append(truss, "fragment", data.getFragment());
        textView.setText(truss.build());
    }

    private static void fillExtras(@Nullable Intent intent, @NonNull TextView textView) {
        Bundle extras = intent == null ? null : intent.getExtras();
        if (extras == null) {
            textView.setText(Printer.EMPTY);
            return;
        }

        Truss truss = new Truss();
        for (String key : extras.keySet()) {
            Printer.appendWithClass(truss, key, extras.get(key));
        }
        textView.setText(truss.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Launch").setIcon(R.drawable.ic_launch_white_48dp).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(Intent.createChooser(new Intent(getIntent()).setComponent(null), null));
                return true;
            }
        });
        menu.add("Settings").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                return true;
            }
        });
        return true;
    }
}