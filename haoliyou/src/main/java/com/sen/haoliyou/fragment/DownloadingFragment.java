/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sen.haoliyou.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.mozillaonline.providers.DownloadManager;
import com.mozillaonline.providers.downloads.DownloadSelectListener;
import com.mozillaonline.providers.downloads.ui.DownloadAdapter;
import com.sen.haoliyou.R;
import com.sen.haoliyou.mode.DownloadFileHistory;
import com.sen.haoliyou.tools.ResourcesUtils;
import com.sen.haoliyou.widget.BaseDialogCumstorTip;
import com.sen.haoliyou.widget.CustomerDialog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * View showing a list of all downloads the Download Manager knows about.
 */
public class DownloadingFragment extends Fragment implements
        OnItemClickListener, DownloadSelectListener, OnClickListener,
        OnCancelListener {
    private static final String LOG_TAG = "DownloadList";
    private ListView mSizeOrderedListView;
    private View mEmptyView;
    private ViewGroup mSelectionMenuView;
    private Button mSelectionDeleteButton;

    private DownloadManager mDownloadManager;
    private Cursor mSizeSortedCursor;
    private DownloadAdapter mSizeSortedAdapter;
    private MyContentObserver mContentObserver = new MyContentObserver();
    private MyDataSetObserver mDataSetObserver = new MyDataSetObserver();

    private int mStatusColumnId;
    private int mIdColumnId;
    private int mLocalUriColumnId;
    private int mMediaTypeColumnId;
    private int mReasonColumndId;

    private Set<Long> mSelectedIds = new HashSet<Long>();

    /**
     * We keep track of when a dialog is being displayed for a pending download,
     * because if that download starts running, we want to immediately hide the
     * dialog.
     */
    private Long mQueuedDownloadId = null;
    private AlertDialog mQueuedDialog;

    private class MyContentObserver extends ContentObserver {
        public MyContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            handleDownloadsChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            // may need to switch to or from the empty view
            chooseListToShow();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.download_list, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDownloadManager = new DownloadManager(getActivity()
                .getContentResolver(), getActivity().getPackageName());
        mDownloadManager.setAccessAllDownloads(true);
        DownloadManager.Query baseQuery = new DownloadManager.Query()
                .setOnlyIncludeVisibleInDownloadsUi(true);
        baseQuery
                .setFilterByStatus(DownloadManager.STATUS_RUNNING
                        | DownloadManager.STATUS_PENDING
                        | DownloadManager.STATUS_FAILED
                        | DownloadManager.STATUS_PAUSED);

        mSizeSortedCursor = mDownloadManager.query(baseQuery.orderBy(
                DownloadManager.COLUMN_TOTAL_SIZE_BYTES,
                DownloadManager.Query.ORDER_DESCENDING));

        // only attach everything to the listbox if we can access the download
        // database. Otherwise,
        // just show it empty
        if (haveCursors()) {
            getActivity().startManagingCursor(mSizeSortedCursor);

            mStatusColumnId = mSizeSortedCursor
                    .getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
            mIdColumnId = mSizeSortedCursor
                    .getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
            mLocalUriColumnId = mSizeSortedCursor
                    .getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
            mMediaTypeColumnId = mSizeSortedCursor
                    .getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
            mReasonColumndId = mSizeSortedCursor
                    .getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);

            mSizeSortedAdapter = new DownloadAdapter(getActivity(),
                    mSizeSortedCursor, this);
            mSizeOrderedListView.setAdapter(mSizeSortedAdapter);

        }

        chooseListToShow();
    }

    private void setupViews(View view) {

        mSizeOrderedListView = (ListView) view
                .findViewById(R.id.size_ordered_list);
        mSizeOrderedListView.setOnItemClickListener(this);
        mEmptyView = view.findViewById(R.id.empty);
        mSelectionMenuView = (ViewGroup) view.findViewById(R.id.selection_menu);
        mSelectionDeleteButton = (Button) view
                .findViewById(R.id.selection_delete);
        mSelectionDeleteButton.setText("删除");
        mSelectionDeleteButton.setOnClickListener(this);
        Button deselect_all = (Button) view.findViewById(R.id.deselect_all);
        deselect_all.setText("取消");
        deselect_all.setOnClickListener(this);
    }

    private boolean haveCursors() {
        return mSizeSortedCursor != null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (haveCursors()) {
            mSizeSortedCursor.registerContentObserver(mContentObserver);
            mSizeSortedCursor.registerDataSetObserver(mDataSetObserver);
            refresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (haveCursors()) {
            mSizeSortedCursor.unregisterContentObserver(mContentObserver);
            mSizeSortedCursor.unregisterDataSetObserver(mDataSetObserver);
        }
    }

    // 把状态先屏蔽一下没出什么事，因为要转移到fragment中
    /*
     * @Override protected void onSaveInstanceState(Bundle outState) {
	 * super.onSaveInstanceState(outState); outState.putLongArray("selection",
	 * getSelectionAsArray()); }
	 */

    private long[] getSelectionAsArray() {
        long[] selectedIds = new long[mSelectedIds.size()];
        Iterator<Long> iterator = mSelectedIds.iterator();
        for (int i = 0; i < selectedIds.length; i++) {
            selectedIds[i] = iterator.next();
        }
        return selectedIds;
    }

	/*
     * @Override protected void onRestoreInstanceState(Bundle
	 * savedInstanceState) { super.onRestoreInstanceState(savedInstanceState);
	 * mSelectedIds.clear(); for (long selectedId :
	 * savedInstanceState.getLongArray("selection")) {
	 * mSelectedIds.add(selectedId); } chooseListToShow();
	 * showOrHideSelectionMenu(); }
	 */

    /**
     * Show the correct ListView and hide the other, or hide both and show the
     * empty view.
     */
    private void chooseListToShow() {
        mSizeOrderedListView.setVisibility(View.GONE);

        if (mSizeSortedCursor == null || mSizeSortedCursor.getCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            activeListView().setVisibility(View.VISIBLE);
            activeListView().invalidateViews(); // ensure checkboxes get updated
        }
    }

    /**
     * @return the ListView that should currently be visible.
     */
    private ListView activeListView() {
        return mSizeOrderedListView;
    }

    /**
     * @return an OnClickListener to delete the given downloadId from the
     * Download Manager
     */
    private DialogInterface.OnClickListener getDeleteClickHandler(final long downloadId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDownload(downloadId);
            }
        };
    }


    /**
     * @return an OnClickListener to restart the given downloadId in the
     * Download Manager
     */
    private DialogInterface.OnClickListener getRestartClickHandler(
            final long downloadId) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDownloadManager.restartDownload(downloadId);
            }
        };
    }

    private void handleItemClick(Cursor cursor) {
        long id = cursor.getInt(mIdColumnId);
        switch (cursor.getInt(mStatusColumnId)) {
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
                mDownloadManager.pauseDownload(id);
                break;

            case DownloadManager.STATUS_PAUSED:
                if (isPausedForWifi(cursor)) {
                    mQueuedDownloadId = id;
                    mQueuedDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.dialog_title_queued_body)
                            .setMessage(R.string.dialog_queued_body)
                            .setPositiveButton(R.string.keep_queued_download, null)
                            .setNegativeButton(R.string.remove_download,
                                    getDeleteClickHandler(id))
                            .setOnCancelListener(this).show();
                } else {
                    mDownloadManager.resumeDownload(id);
                }
                break;

            case DownloadManager.STATUS_FAILED:
                showFailedDialog(id, getErrorMessage(cursor));
                break;
        }
    }

    /**
     * @return the appropriate error message for the failed download pointed to
     * by cursor
     */
    private String getErrorMessage(Cursor cursor) {
        switch (cursor.getInt(mReasonColumndId)) {
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                if (isOnExternalStorage(cursor)) {
                    return getString(R.string.dialog_file_already_exists);
                } else {
                    // the download manager should always find a free filename for
                    // cache downloads,
                    // so this indicates a strange internal error
                    return getUnknownErrorMessage();
                }

            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                if (isOnExternalStorage(cursor)) {
                    return getString(R.string.dialog_insufficient_space_on_external);
                } else {
                    return getString(R.string.dialog_insufficient_space_on_cache);
                }

            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                return getString(R.string.dialog_media_not_found);

            case DownloadManager.ERROR_CANNOT_RESUME:
                return getString(R.string.dialog_cannot_resume);
            default:
                return getUnknownErrorMessage();
        }
    }

    private boolean isOnExternalStorage(Cursor cursor) {
        String localUriString = cursor.getString(mLocalUriColumnId);
        if (localUriString == null) {
            return false;
        }
        Uri localUri = Uri.parse(localUriString);
        if (!localUri.getScheme().equals("file")) {
            return false;
        }
        String path = localUri.getPath();
        String externalRoot = Environment.getExternalStorageDirectory()
                .getPath();
        return path.startsWith(externalRoot);
    }

    private String getUnknownErrorMessage() {
        return getString(R.string.dialog_failed_body);
    }


    //    private void showPausedDialog(long downloadId) {
//        new AlertDialog.Builder(getActivity())
//                .setTitle(R.string.download_queued)
//                .setMessage(R.string.dialog_paused_body)
//                .setNegativeButton(R.string.delete_download,
//                        getDeleteClickHandler(downloadId))
//                .setPositiveButton(R.string.resume_download,
//                        getResumeClickHandler(downloadId)).show();
//    }
//DialogButtonOnclickLinster dialogListener, Context context, String title, String msg, String leftBtnStr, String rightBtnStrl, boolean isTitleShow, boolean isMsgShow) {
//        this.mDialogListener = dialogListener
    private void showFailedDialog(final long downloadId, String dialogBody) {

        BaseDialogCumstorTip.getDefault().showTwoBtnDialog(new BaseDialogCumstorTip.DialogButtonOnclickLinster() {
                                                               @Override
                                                               public void onLeftButtonClick(CustomerDialog dialog) {
                                                                   dialog.dismiss();
                                                                   mDownloadManager.restartDownload(downloadId);
                                                               }

                                                               @Override
                                                               public void onRigthButtonClick(CustomerDialog dialog) {
                                                                   dialog.dismiss();
                                                                   deleteDownload(downloadId);
                                                                   new Delete().from(DownloadFileHistory.class).where("downloadid = ?", downloadId).execute();

                                                               }
                                                           }, getActivity(), ResourcesUtils.getResString(getActivity(), R.string.dialog_title_not_available),
                dialogBody, ResourcesUtils.getResString(getActivity(), R.string.retry_download),
                ResourcesUtils.getResString(getActivity(), R.string.delete_download), true, true);
      /*  new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_not_available)
                .setMessage(dialogBody)
                .setNegativeButton(R.string.delete_download,
                        getDeleteClickHandler(downloadId))
                .setPositiveButton(R.string.retry_download,
                        getRestartClickHandler(downloadId)).show();*/
    }

    // handle a click from the size-sorted list
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        mSizeSortedCursor.moveToPosition(position);
        handleItemClick(mSizeSortedCursor);
    }

    // handle a click on one of the download item checkboxes
    @Override
    public void onDownloadSelectionChanged(long downloadId, boolean isSelected) {

        if (isSelected) {

            mSelectedIds.add(downloadId);
        } else {

            mSelectedIds.remove(downloadId);

        }
        showOrHideSelectionMenu();
    }

    // 显示和隐藏底部的选择按钮
    private void showOrHideSelectionMenu() {
        boolean shouldBeVisible = !mSelectedIds.isEmpty();
        boolean isVisible = mSelectionMenuView.getVisibility() == View.VISIBLE;
        if (shouldBeVisible) {
            updateSelectionMenu();
            if (!isVisible) {
                // show menu
                mSelectionMenuView.setVisibility(View.VISIBLE);
                mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), R.anim.footer_appear));

            }
        } else if (!shouldBeVisible && isVisible) {
            // hide menu
            mSelectionMenuView.setVisibility(View.GONE);
            mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(
                    getActivity(), R.anim.footer_disappear));
        }
    }

    /**
     * Set up the contents of the selection menu based on the current selection.
     */
    private void updateSelectionMenu() {
        int deleteButtonStringId = R.string.delete_download;
        if (mSelectedIds.size() == 1) {
            Cursor cursor = mDownloadManager.query(new DownloadManager.Query()
                    .setFilterById(mSelectedIds.iterator().next()));
            try {
                cursor.moveToFirst();
                switch (cursor.getInt(mStatusColumnId)) {
                    case DownloadManager.STATUS_FAILED:
                        deleteButtonStringId = R.string.delete_download;
                        break;

                    case DownloadManager.STATUS_PENDING:
                        deleteButtonStringId = R.string.remove_download;
                        break;

                    case DownloadManager.STATUS_PAUSED:
                    case DownloadManager.STATUS_RUNNING:
                        deleteButtonStringId = R.string.cancel_running_download;
                        break;
                }
            } finally {
                cursor.close();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selection_delete:
                BaseDialogCumstorTip.getDefault().showOneMsgTwoBtnDilog(new BaseDialogCumstorTip.DialogButtonOnclickLinster() {
                    @Override
                    public void onLeftButtonClick(CustomerDialog dialog) {
                        dialog.dismiss();
                        for (Long downloadId : mSelectedIds) {
                            deleteDownload(downloadId);
                        }

                        ActiveAndroid.beginTransaction();
                        try {
                            for (Long downloadId : mSelectedIds) {
                                new Delete().from(DownloadFileHistory.class).where("downloadid = ?", downloadId).execute();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                        clearSelection();
                    }

                    @Override
                    public void onRigthButtonClick(CustomerDialog dialog) {
                        dialog.dismiss();
                        clearSelection();
                    }
                }, getContext(), "确定删除?", "确定", "取消");


                break;

            case R.id.deselect_all:
                clearSelection();
                break;
        }
    }

    /**
     * Requery the database and update the UI.
     */
    private void refresh() {
        mSizeSortedCursor.requery();
    }

    private void clearSelection() {
        mSelectedIds.clear();
        showOrHideSelectionMenu();
    }

    /**
     * Delete a download from the Download Manager.
     */
    private void deleteDownload(long downloadId) {
        if (moveToDownload(downloadId)) {
            int status = mSizeSortedCursor.getInt(mStatusColumnId);
            boolean isComplete = status == DownloadManager.STATUS_SUCCESSFUL
                    || status == DownloadManager.STATUS_FAILED;
            String localUri = mSizeSortedCursor.getString(mLocalUriColumnId);
            if (isComplete && localUri != null) {
                String path = Uri.parse(localUri).getPath();
                if (path.startsWith(Environment.getExternalStorageDirectory()
                        .getPath())) {
                    mDownloadManager.markRowDeleted(downloadId);
                    return;
                }
            }
        }
        mDownloadManager.remove(downloadId);

    }

    @Override
    public boolean isDownloadSelected(long id) {
        return mSelectedIds.contains(id);
    }

    /**
     * Called when there's a change to the downloads database.
     */
    void handleDownloadsChanged() {
        checkSelectionForDeletedEntries();

        if (mQueuedDownloadId != null && moveToDownload(mQueuedDownloadId)) {
            if (mSizeSortedCursor.getInt(mStatusColumnId) != DownloadManager.STATUS_PAUSED
                    || !isPausedForWifi(mSizeSortedCursor)) {
                mQueuedDialog.cancel();
            }
        }
    }

    private boolean isPausedForWifi(Cursor cursor) {
        return cursor.getInt(mReasonColumndId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI;
    }

    /**
     * Check if any of the selected downloads have been deleted from the
     * downloads database, and remove such downloads from the selection.
     */
    private void checkSelectionForDeletedEntries() {
        // gather all existing IDs...
        Set<Long> allIds = new HashSet<Long>();
        for (mSizeSortedCursor.moveToFirst(); !mSizeSortedCursor.isAfterLast(); mSizeSortedCursor
                .moveToNext()) {
            allIds.add(mSizeSortedCursor.getLong(mIdColumnId));
        }

        // ...and check if any selected IDs are now missing
        for (Iterator<Long> iterator = mSelectedIds.iterator(); iterator
                .hasNext(); ) {
            if (!allIds.contains(iterator.next())) {
                iterator.remove();
            }
        }
    }

    /**
     * Move {to the download with the given ID.
     *
     * @return true if the specified download ID was found; false otherwise
     */
    private boolean moveToDownload(long downloadId) {
        for (mSizeSortedCursor.moveToFirst(); !mSizeSortedCursor.isAfterLast(); mSizeSortedCursor
                .moveToNext()) {
            if (mSizeSortedCursor.getLong(mIdColumnId) == downloadId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when a dialog for a pending download is canceled.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        mQueuedDownloadId = null;
        mQueuedDialog = null;
    }

}
