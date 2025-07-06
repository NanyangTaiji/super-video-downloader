package com.myAllVideoBrowser.util;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.myAllVideoBrowser.R;

import java.io.File;
import java.util.List;

public class IntentUtil {

    private FileUtil fileUtil;

    public IntentUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Deprecated
    public void openVideoFolder(Context context, String path) {
        if (context == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File dir = new File(context.getFilesDir(), FileUtil.FOLDER_NAME);
        Uri photoURI = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                dir
        );
        intent.setDataAndType(photoURI, DocumentsContract.Document.MIME_TYPE_DIR);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(
                    context,
                    context.getString(R.string.settings_message_open_folder),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void shareVideo(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        boolean fileSupported = fileUtil.isFileApiSupportedByUri(context, uri);

        if (fileSupported) {
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    new File(uri.getPath())
            );
            intent.setDataAndType(fileUri, "video/mp4");
            intent.setClipData(ClipData.newRawUri("", fileUri));
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        } else {
            intent.setClipData(ClipData.newRawUri("", uri));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY
        );
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        }

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "Share via:"));
        } else {
            Toast.makeText(
                    context,
                    context.getString(R.string.video_share_message),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
