package com.koalabeast.tagpro;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
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
	 */
	public class LeaderBoardFragment extends Fragment implements OnClickListener {
		public static final String ARG_POSITION = "position";
		private int position;
		private View rootView;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_leader, container, false);
			
			Bundle args = getArguments();
			position = args.getInt(ARG_POSITION);
			
			// Set the server name and location that the leader boards are for.
			TextView srvName = (TextView) rootView.findViewById(R.id.server_name);
			srvName.setText(LeaderActivity.this.server.name);
			TextView srvLoc = (TextView) rootView.findViewById(R.id.server_location);
			srvLoc.setText(LeaderActivity.this.server.location);
			
			// Create an options menu for the fragment
			setHasOptionsMenu(true);

			return rootView;
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.leader, menu);
		}
		
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
		
		@Override
		public void onStart() {
			super.onStart();
			startAsyncTask();
		}
		
		/**
		 * Refresh the data for the leader boards.
		 */
		private void refresh() {
			// TODO - Remove current data and make the icon rotate during refresh, in place of the toast.
			Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_LONG).show();
			startAsyncTask();
		}
		
		/**
		 * Kick off the asynchronous task to either load or refresh the data.
		 */
		private void startAsyncTask() {
			new LeaderBoardParser(this, LeaderActivity.this.leaderBoards[position]).execute(server.url);
		}
		
		/**
		 * Callback for the parser completion to trigger the actions.
		 * 
		 * @param result - The List of leader infos for the leader bard.
		 * @param previousWinner - The previous winner of this specific leader board.
		 */
		public void onParserComplete(List<LeaderInfo> result, String previousWinner) {
			try {
				// This is the fragment's main content container.
				LinearLayout leaderViewer = (LinearLayout) rootView.findViewById(R.id.leader_viewer);
				
				// Create/Customize a new view that we will be adding all the users on the leader board to.
				LinearLayout leaderLayout = new LinearLayout(getActivity());
				LayoutParams layParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				leaderLayout.setLayoutParams(layParams);
				leaderLayout.setOrientation(LinearLayout.VERTICAL);
				
				// Generate and add all users on the leader board to the new view.
				for (LeaderInfo li : result) {
					leaderLayout.addView(generateLeaderInfoView(li));
				}
				
				// Remove the last element, either the loader wheel or the linear layout we made to hold all the players.
				leaderViewer.removeViewAt(leaderViewer.getChildCount() - 1);
				
				// Highlight the previous winner's awesomeness.
				TextView prevWinText = (TextView) rootView.findViewById(R.id.server_previouswinner);
				prevWinText.setText("Previous Winner: " + previousWinner);
				
				// Add the new view to the page all at once.
				leaderViewer.addView(leaderLayout);
			}
			catch (NullPointerException e) {
				onParserError(); // Fall out and let the user know.
			}
		}
		
		/**
		 * Callback for the parser completion, with error, to alert the user that something went wrong.
		 * 
		 * TODO - Show a dialog (rather than a toast) to alert and provide the option to either retry or cancel and back out.
		 */
		public void onParserError() {
			Toast.makeText(getActivity(), "Error retrieving leader board.", Toast.LENGTH_SHORT).show();
		}
		
		/**
		 * Generate and return the view for a leader info object.
		 * 
		 * @param li - The single leader's info object.
		 */
		private View generateLeaderInfoView(LeaderInfo li) {
			// Use the player_view template to create the player view
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.player_view, null, false);
			view.setOnClickListener(this);
			
			TextView rank = (TextView) view.findViewById(R.id.player_rank);
			rank.setText(Integer.toString(li.getRank()) + ".");
			TextView name = (TextView) view.findViewById(R.id.player_name);
			name.setText(li.getName());
			TextView points = (TextView) view.findViewById(R.id.player_points);
			points.setText(Integer.toString(li.getPoints()));
			
			// Add a tag to the view for easy info retrieval (specifically used in the onClick for profile loading)
			view.setTag(li);
			
			return view;
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
