/*
 * Copyright (C) 2017 Chris Tarazi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package app.CT.BTCCalculator.activities;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import app.CT.BTCCalculator.BuildConfig;
import app.CT.BTCCalculator.R;

public class AboutActivity extends AppCompatActivity {

    RelativeLayout rootLayout;
    ImageView icon;
    TextView appVersion;
    TextView githubLink;
    HtmlTextView htmlAbout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(String.format("%s %s", getString(R.string.about), getString(R.string.app_name)));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        rootLayout = (RelativeLayout) findViewById(R.id.activityLayout);
        icon = (ImageView) rootLayout.findViewById(R.id.appIcon);
        try {
            icon.setImageDrawable(this.getPackageManager().getApplicationIcon("app.CT.BTCCalculator"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appVersion = (TextView) rootLayout.findViewById(R.id.appVersion);
        appVersion.setText(String.format("Version: %s", BuildConfig.VERSION_NAME));

        githubLink = (TextView) rootLayout.findViewById(R.id.githubLink);
        githubLink.setMovementMethod(LinkMovementMethod.getInstance());

        String htmlLink = "<a href='https://github.com/christarazi/btcapp'> Source code</a>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            githubLink.setText(Html.fromHtml(htmlLink, Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            githubLink.setText(Html.fromHtml(htmlLink));
        }

        htmlAbout = (HtmlTextView) rootLayout.findViewById(R.id.htmlAbout);
        htmlAbout.setHtml(R.raw.about, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                return null;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
