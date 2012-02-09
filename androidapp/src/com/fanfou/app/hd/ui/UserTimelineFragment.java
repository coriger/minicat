package com.fanfou.app.hd.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.db.FanFouProvider;
import com.fanfou.app.hd.db.Contents.StatusInfo;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.09
 * 
 */
public class UserTimelineFragment extends BaseTimlineFragment {
	private static final String TAG = UserTimelineFragment.class
			.getSimpleName();
	private String userId;

	public static UserTimelineFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_ID, userId);
		UserTimelineFragment fragment = new UserTimelineFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data=getArguments();
		if(data!=null){
			userId=data.getString(Constants.EXTRA_ID);
		}
		if(StringHelper.isEmpty(userId)){
			userId=App.getUserId();
		}
		
		if (App.DEBUG) {
			Log.d(TAG, "onCreate() userId="+userId);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected int getType() {
		return Constants.TYPE_STATUSES_USER_TIMELINE;
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
		}
		final ResultHandler handler = new ResultHandler(this);
		final Cursor cursor = getCursor();
		String sinceId = null;
		String maxId = null;
		if (doGetMore) {
			maxId = Utils.getMaxId(cursor);
		} else {
			sinceId = Utils.getSinceId(cursor);
		}
		FanFouService.doFetchUserTimeline(getActivity(), new Messenger(handler), userId, sinceId, maxId);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = StatusInfo.CONTENT_URI;
		String selection = StatusInfo.TYPE + " =? AND " + StatusInfo.USER_ID
				+ " =? ";
		String[] selectionArgs = new String[] { String.valueOf(getType()), userId};
		String sortOrder=FanFouProvider.ORDERBY_DATE_DESC;
		CursorLoader loader=new CursorLoader(getActivity(), uri, null, selection, selectionArgs, sortOrder);
		return loader;
	}

}