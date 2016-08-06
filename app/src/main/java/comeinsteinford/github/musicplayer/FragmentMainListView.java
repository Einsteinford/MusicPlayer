package comeinsteinford.github.musicplayer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by KK on 2016-07-31.
 */
public class FragmentMainListView extends Fragment implements AdapterView.OnItemClickListener {
    private ArrayList<ItemMusicList> mList;
    private ListView mMusicListView;
    private AdapterMusicList mAdapterMusicList;
    private static String TAG = "FragmentMainListView";
    private MainActivity mMainActivity;
    private String[] mFileNames;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initMusicList();    //初始化数据

        mMainActivity = (MainActivity) getActivity();
        //取得主活动实例
        mAdapterMusicList = new AdapterMusicList(activity, R.layout.item_music_list, mList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_listview, container, false);
        //传入listView列表所存在的xml布局文件
        mMusicListView = (ListView) view.findViewById(R.id.music_list_view);
        //从布局文件中读取listView实例
        mMusicListView.setAdapter(mAdapterMusicList);
        //然后关联Adapter
        mMusicListView.setOnItemClickListener(this);
        return view;

    }


    private void initMusicList() {
        mList = new ArrayList<>();
        try {
            mFileNames = getActivity().getAssets().list("music");
        } catch (IOException e) {
            e.printStackTrace();
        }//获取资源文件夹下的音乐目录

        for (String str : mFileNames) {
            switch (str) {
                case "desperado.mp3":
                    mList.add(new ItemMusicList("Desperado", "藤田惠美", R.drawable.ic_launcher,"desperado.mp3"));
                    break;
                case "imagine.mp3":
                    mList.add(new ItemMusicList("Imagine", "John Lennon", R.drawable.ic_launcher,"imagine.mp3"));
                    break;
                case "photograph.mp3":
                    mList.add(new ItemMusicList("Photograph", "Ed Sheeran", R.drawable.ic_launcher,"photograph.mp3"));
                    break;
                case "remember.mp3":
                    mList.add(new ItemMusicList("记得", "萧敬腾", R.drawable.ic_launcher,"remember.mp3"));
                    break;
            }
            Log.i(TAG, "initMusicList: Done");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //parent代表父类listView,view代表被点击的item
        mMainActivity.setMusicPosition(position);

    }

    public ArrayList<ItemMusicList> getList() {     //获取整个播放列表
        return mList;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
