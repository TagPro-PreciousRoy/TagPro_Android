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

public class LeaderActivity extends FragmentActivity {
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private String[] Boards;
	private ServerInfo server;
	private List<List<LeaderInfo>> leaderboards = new ArrayList<List<LeaderInfo>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leader);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Leader Boards");

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		Boards = getResources().getStringArray(R.array.leader_queries);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_POSITION, position);
			fragment.setArguments(args);
			return fragment;
		}

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

	public static class DummySectionFragment extends Fragment {
		public static final String ARG_POSITION = "position";
		private int position;

		public DummySectionFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_leader, container, false);
			
			position = getArguments().getInt(ARG_POSITION);
			//TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			//dummyTextView.setText(Integer.toString(position));
			return rootView;
		}
		
		@Override
		public void onStart() {
			super.onStart();
		}
	}

}
