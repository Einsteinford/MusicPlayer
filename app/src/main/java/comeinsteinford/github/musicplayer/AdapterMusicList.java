package comeinsteinford.github.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by KK on 2016-07-31.
 */
public class AdapterMusicList extends ArrayAdapter<ItemMusicList> {
    int mResourceId;

    public AdapterMusicList(Context context, int resource, List<ItemMusicList> objects) {
        //resource代表Item的布局文件
        super(context, resource, objects);
        mResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemMusicList item = getItem(position);//取得item实例

        ViewHolder mViewHolder;
        if (convertView == null){
            //如果view未被创建过，且缓存为空
            mViewHolder = new ViewHolder();
            convertView= LayoutInflater.from(getContext()).inflate(mResourceId,null);
            //此时，将mResourceId转换成了View类
            //inflate的第一个参数是布局文件Id，第二个ViewGroup对象，在创建单独的view的时候，只需要填null

            mViewHolder.musicTitle = (TextView)convertView.findViewById(R.id.music_title);
            mViewHolder.musicSinger = (TextView) convertView.findViewById(R.id.music_singer);
            mViewHolder.musicImage = (ImageView) convertView.findViewById(R.id.music_image);
            //通过convertView找到其中的控件

            convertView.setTag(mViewHolder);
            //将ViewHolder类存储到已经实例化的convertView中
        }else {
            //如果convertView有值
            mViewHolder = (ViewHolder) convertView.getTag();
            //从convertView中拿到ViewHolder类，从而可以使用其中的对象
        }
        mViewHolder.musicTitle.setText(item.getMusicTitle());
        mViewHolder.musicImage.setImageResource(item.getImageId());
        mViewHolder.musicSinger.setText(item.getSinger());
        //通过实例化后的ItemMusicList数据，获取相应函数，进行属性的赋值

        return convertView;
    }
    class ViewHolder{
        //创建一个类，用于保存实例化的对象

        private TextView musicTitle;
        private TextView musicSinger;
        private ImageView musicImage;

    }
}
