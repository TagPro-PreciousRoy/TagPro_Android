package com.koalabeast.tagpro;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.koalabeast.tagpro.infocontainers.LeaderInfo;
import com.koalabeast.tagpro.infocontainers.ServerInfo;
import com.koalabeast.tagpro.parsers.LeaderBoardParser;

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
	private String[] leaderBoards;
	private ServerInfo server;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the arguments passed in, i.e. the server to query.
		Bundle b = getIntent().getExtras();
		this.server = b.getParcelable("server");
		
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
		leaderBoards = getResources().getStringArray(R.array.leader_queries);
		
		// Start the parsing service in the background.
		//new LeaderBoardParser(this).execute(server.url);
	}
	
	public void onParserComplete(List<List<LeaderInfo>> li, String[] previousWinners) {
		
	}
	
	/**
	 * Create a new LinearLayout view for a leader pane.
	 */
	private LinearLayout createLeaderPane(LeaderInfo leaderInfo) {
		LinearLayout ll = new LinearLayout(this);
		
		return ll;
	}

	/**
	 * Simple adapter to create a fragmented page view with scrollable tabs.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public Fragment[] myFragments = new Fragment[3];

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
			myFragments[position] = fragment;
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
			
			if (position < LeaderActivity.this.leaderBoards.length) {
				return LeaderActivity.this.leaderBoards[position].toUpperCase(l);
			}
			
			return null;
		}
	}

	/**
	 * Create the fragment views for the given position, as passed in from the bundle args.
	 */
	public class LeaderBoardFragment extends Fragment {
		public static final String ARG_POSITION = "position";
		private int position;
		private View rootView;

		public LeaderBoardFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_leader, container, false);
			
			Bundle args = getArguments();
			position = args.getInt(ARG_POSITION);
			
			TextView srvName = (TextView) rootView.findViewById(R.id.server_name);
			srvName.setText(LeaderActivity.this.server.name);
			TextView srvLoc = (TextView) rootView.findViewById(R.id.server_location);
			srvLoc.setText(LeaderActivity.this.server.location);
			return rootView;
		}
		
		@Override
		public void onStart() {
			super.onStart();
			
			new LeaderBoardParser(this).execute(server.url);
		}
		
		@Override
		public void onResume() {
			super.onResume();
		}
		
		public void onParserComplete(List<List<LeaderInfo>> result, String[] prevWinners) {
			ScrollView sv = (ScrollView) rootView.findViewById(R.id.leader_scroller);
			sv.removeAllViews();
			
			List<LeaderInfo> liList = result.get(position);
			LinearLayout ll = new LinearLayout(getActivity());
			for(LeaderInfo li : liList) {
				TextView tv = new TextView(getActivity());
				tv.setText(Integer.toString(li.getRank()));
				ll.addView(tv);
			}
			sv.addView(ll);
		}
	}
}
