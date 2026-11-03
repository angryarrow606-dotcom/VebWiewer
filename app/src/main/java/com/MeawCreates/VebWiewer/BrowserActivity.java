package com.MeawCreates.VebWiewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;

public class BrowserActivity extends AppCompatActivity {
  WebView web;
  EdgeLoader loader;
  boolean adBlockOn = true;
  static final String[] BLOCKED = {
    "doubleclick.net", "googlesyndication.com", "adservice.google.com",
    "googleadservices.com", "popads.net", "adnxs.com", "taboola.com",
    "outbrain.com", "propellerads.com", "exoclick.com"
  };

  static final String HIDE_WEBVIEW_JS =
    "try{if(!window.chrome){window.chrome={runtime:{}};}}catch(e){}";

  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    if (getSupportActionBar() != null) getSupportActionBar().hide();
    adBlockOn = getSharedPreferences("s", 0).getBoolean("adblock", true);

    web = new WebView(this);
    loader = new EdgeLoader(this);
    FrameLayout root = new FrameLayout(this);
    root.addView(web, new FrameLayout.LayoutParams(-1, -1));
    root.addView(loader, new FrameLayout.LayoutParams(-1, -1));
    setContentView(root);

    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setDatabaseEnabled(true);
    s.setSupportMultipleWindows(true);
    s.setJavaScriptCanOpenWindowsAutomatically(false);
    s.setUseWideViewPort(true);
    s.setLoadWithOverviewMode(true);
    s.setMediaPlaybackRequiresUserGesture(false);
    s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

    String ua = s.getUserAgentString();
    ua = ua.replace("; wv", "").replace("Version/4.0 ", "");
    s.setUserAgentString(ua);

    CookieManager cm = CookieManager.getInstance();
    cm.setAcceptCookie(true);
    cm.setAcceptThirdPartyCookies(web, true);

    web.setWebChromeClient(new WebChromeClient() {
      @Override public void onProgressChanged(WebView v, int progress) {
        loader.setProgress(progress);
      }

      @Override public boolean onCreateWindow(WebView v, boolean dialog,
                                              boolean userGesture, Message msg) {
        if (adBlockOn && !userGesture) return false;
        WebView temp = new WebView(BrowserActivity.this);
        temp.setWebViewClient(new WebViewClient() {
          @Override public boolean shouldOverrideUrlLoading(WebView x, String u) {
            web.loadUrl(u);
            return true;
          }
          @Override public void onPageStarted(WebView x, String u, Bitmap f) {
            if (u != null && !u.equals("about:blank")) {
              web.loadUrl(u);
              x.stopLoading();
            }
          }
        });
        WebView.WebViewTransport t = (WebView.WebViewTransport) msg.obj;
        t.setWebView(temp);
        msg.sendToTarget();
        return true;
      }
    });

    web.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView v, String u) {
        if (u.startsWith("http://") || u.startsWith("https://")) return false;
        if (u.startsWith("tel:") || u.startsWith("mailto:")) {
          try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(u)));
          } catch (Exception e) {}
        }
        return true;
      }

      @Override public WebResourceResponse shouldInterceptRequest(WebView v,
                                                                  WebResourceRequest r) {
        if (!adBlockOn) return null;
        String h = r.getUrl().getHost();
        if (h != null) {
          for (String ad : BLOCKED) {
            if (h.contains(ad)) {
              return new WebResourceResponse("text/plain", "utf-8",
                  new ByteArrayInputStream(new byte[0]));
            }
          }
        }
        return null;
      }

      @Override public void onReceivedError(WebView v, WebResourceRequest r,
                                            WebResourceError e) {
        if (r.isForMainFrame()) {
          v.loadData("<html><body style='font-family:sans-serif;padding:24px'>"
              + "<h2>Couldn't load this page</h2><p>" + e.getDescription()
              + "</p><p>" + r.getUrl() + "</p></body></html>",
              "text/html", "UTF-8");
        }
      }

      @Override public void onReceivedHttpError(WebView v, WebResourceRequest r,
                                                WebResourceResponse res) {
        if (r.isForMainFrame()) {
          Toast.makeText(BrowserActivity.this,
              "Site replied with error " + res.getStatusCode(),
              Toast.LENGTH_LONG).show();
        }
      }

      @Override public void onPageStarted(WebView v, String url, Bitmap f) {
        loader.start();
        v.evaluateJavascript(HIDE_WEBVIEW_JS, null);
      }

      @Override public void onPageFinished(WebView v, String url) {
        loader.finish();
        v.evaluateJavascript(HIDE_WEBVIEW_JS, null);
        History.add(BrowserActivity.this, url);
      }
    });

    web.loadUrl(getIntent().getStringExtra("url"));
  }

  @Override public void onBackPressed() {
    if (web.canGoBack()) web.goBack();
    else super.onBackPressed();
  }
}