package com.koalabeast.tagpro;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koalabeast.tagpro.infocontainers.LeaderInfo;
import com.koalabeast.tagpro.infocontainers.ServerInfo;

/**
 * 
 * @author nerdwaller
 *
 * @description The leader activity displays the leader boards for the provided server.  The class
 * should be pretty easily scaled for additional leader board additions through the leader_queries
 * array in the @string file.
 * 
 */
public class LeaderActivity extends FragmentActivity {
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private String[] Boards;
	private ServerInfo server;
	private List<List<LeaderInfo>> leaderboards = new ArrayList<List<LeaderInfo>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set the default layout, the fragments will determine their own layout within.
		setContentView(R.layout.activity_leader);
		
		// Customize the action bar.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.title_activity_leader));

		// Set up the scrollable tabs
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Get the page titles for the scrollable tabs.
		Boards = getResources().getStringArray(R.array.leader_queries);
	}

	/**
	 * Simple adapter to create a fragmented page view with scrollable tabs.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * Get the fragment that will be viewed, pass the fragment all needed info to
		 * populate the view.
		 */
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new LeaderBoardFragment();
			Bundle args = new Bundle();
			args.putInt(LeaderBoardFragment.ARG_POSITION, position);
			fragment.setArguments(args);
			return fragment;
		}

		/**
		 * Return the number of pages we will have for the leader board (number of scrollable tabs)
		 */
		@Override
		public int getCount() {
			return getResources().getStringArray(R.array.leader_queries).length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			
			if (position < LeaderActivity.this.Boards.length) {
				return LeaderActivity.this.Boards[position].toUpperCase(l);
			}
			
			return null;
		}
	}

	/**
	 * Create the fragment views for the given position, as passed in from the bundle args.
	 */
	public static class LeaderBoardFragment extends Fragment {
		public static final String ARG_POSITION = "position";
		private int position;

		public LeaderBoardFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_leader, container, false);
			
			Bundle args = getArguments();
			position = args.getInt(ARG_POSITION);

			return rootView;
		}
		
		@Override
		public void onStart() {
			super.onStart();
		}
	}

}
