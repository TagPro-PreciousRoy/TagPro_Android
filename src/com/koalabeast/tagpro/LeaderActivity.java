package com.koalabeast.tagpro;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
@SuppressLint("ValidFragment") // This should probably be fixed, as the fragment may need to be static.
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

		// Get the page titles for the scrollable tabs.
		leaderBoards = getResources().getStringArray(R.array.leader_queries);
				
		// Set up the scrollable tabs
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(leaderBoards.length - 1); // Keep tabs in memory to avoid unnecessary refresh.
	}

	/**
	 * Simple adapter to create a fragmented page view with scrollable tabs.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public Fragment[] myFragments = new Fragment[3];

		/**
		 * 
		 * @param fm
		 */
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
	 * 
	 * TODO - Update LeaderBoardParser to take a parameter of the desired div and only pass back
	 * a single List<LeaderInfo> instead of all the lists.  That's a waste.
	 */
	public class LeaderBoardFragment extends Fragment implements OnClickListener {
		public static final String ARG_POSITION = "position";
		private int position;
		private View rootView;
		
		/**
		 * 
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_leader, container, false);
			
			Bundle args = getArguments();
			position = args.getInt(ARG_POSITION);
			
			TextView srvName = (TextView) rootView.findViewById(R.id.server_name);
			srvName.setText(LeaderActivity.this.server.name);
			TextView srvLoc = (TextView) rootView.findViewById(R.id.server_location);
			srvLoc.setText(LeaderActivity.this.server.location);
			
			// Create an options menu for the fragment
			setHasOptionsMenu(true);

			return rootView;
		}
		
		/**
		 * 
		 */
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.leader, menu);
		}
		
		/**
		 * 
		 */
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				case R.id.action_refresh:
					refresh();
					return true;
				default:
					return super.onOptionsItemSelected(item);
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onStart() {
			super.onStart();
			startAsyncTask();
		}
		
		/**
		 * Refresh the data for the leader boards.
		 */
		private void refresh() {
			// TODO - Make the icon rotate during refresh, remove the toast.
			Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_LONG).show();
			startAsyncTask();
		}
		
		/**
		 * Init the asyncronous task to either load or refresh the data.
		 */
		private void startAsyncTask() {
			new LeaderBoardParser(this).execute(server.url);
		}
		
		/**
		 * Callback for the parser completion to trigger the actions.
		 * 
		 * @param result - The List of lists for leader infos.
		 * @param prevWinners - An array of previous winners.
		 * 
		 * TODO - Pass back a single List<LeaderInfo> and a String for the previous winner.
		 */
		public void onParserComplete(List<List<LeaderInfo>> result, String[] prevWinners) {
			try {
				LinearLayout leaderViewer = (LinearLayout) rootView.findViewById(R.id.leader_viewer);
				
				// Get the list and create the root node that we will add the views to.
				List<LeaderInfo> liList = result.get(position);
				LinearLayout ll = new LinearLayout(getActivity());
				LayoutParams layParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				ll.setLayoutParams(layParams);
				ll.setOrientation(LinearLayout.VERTICAL);
				
				for (LeaderInfo li : liList) {
					ll.addView(generateLeaderInfoView(li));
				}
				
				leaderViewer.removeViewAt(leaderViewer.getChildCount() - 1);
				if (prevWinners.length > position) {
					TextView prevWinTxt = (TextView) rootView.findViewById(R.id.server_previouswinner);
					prevWinTxt.setText("Previous Winner: " + prevWinners[position]);
				}
				
				leaderViewer.addView(ll);
			}
			catch (NullPointerException e) {
				// Just back out
			}
		}
		
		/**
		 * Generate and return the view for a leader info object.
		 * 
		 * @param li - The single leader's info object.
		 */
		private LinearLayout generateLeaderInfoView(LeaderInfo li) {
			// Create and set up the layout view.
			LinearLayout ll = new LinearLayout(getActivity());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 8, 0, 8);
			ll.setLayoutParams(lp);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setBackgroundResource(R.drawable.leaders_rounded_corners);
			ll.setPadding(8, 8, 8, 8);
			ll.setOnClickListener(this);
			
			// Add a tag to the view for easy info retrieval (used in onClick for profile loading)
			ll.setTag(li);
			
			// Create the icon... Design ideas?  TODO - Make this flair, grayed if they don't have it.
			ImageView icon = new ImageView(getActivity());
			LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			iconParams.setMargins(0, 0, 10, 0);
			icon.setLayoutParams(iconParams);
			if ((li.getRank() + 1) % 2 == 0) {
				icon.setImageResource(R.drawable.blue_ball);
			}
			else {
				icon.setImageResource(R.drawable.red_ball);
			}
			ll.addView(icon);
			
			// Set the Rank
			TextView rank = new TextView(getActivity());
			LayoutParams rankParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			rank.setLayoutParams(rankParams);
			rank.setTextColor(Color.parseColor("#FFFFFF"));
			rank.setText(Integer.toString(li.getRank()) + ". ");
			ll.addView(rank);
			
			
			// Set the name
			TextView name = new TextView(getActivity());
			name.setId(100);
			LayoutParams nameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			name.setLayoutParams(nameParams);
			name.setTextColor(Color.parseColor("#FFFFFF"));
			name.setTypeface(Typeface.DEFAULT_BOLD);
			name.setText(li.getName());
			
			ll.addView(name);
			
			return ll;
		}

		/**
		 * Onclick handler, the only things that are clickable on the main pages are the leader info panes.
		 * 
		 * @param view - The view object.
		 */
		@Override
		public void onClick(View view) {
			LinearLayout ll = (LinearLayout) view;
			LeaderInfo li = (LeaderInfo) ll.getTag();
			
			Intent in = new Intent(getActivity(), ProfileViewActivity.class);
			Bundle b = new Bundle();
			b.putParcelable("player", li);
			b.putParcelable("server", LeaderActivity.this.server);
			in.putExtras(b);
			startActivity(in);
		}
	}
}
