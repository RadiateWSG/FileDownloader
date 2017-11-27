package me.spirittalk.filedownloader;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.spirittalk.library.DownloadListener;
import me.spirittalk.library.FileDownloader;
import me.spirittalk.library.ITask;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ListViewAdapter myAdapter;
    private FileDownloader downloader;
    private ITask[] tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileDownloader.init(this);
        initViews();
        downloader = new FileDownloader.Builder().build();
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.listView);
        myAdapter = new ListViewAdapter(this, Constant.videos);
        listView.setAdapter(myAdapter);
    }

    private ITask startTask(String url, final ViewHolder vh) {
        ITask task = downloader.newTask(url);
        task.enqueue(new DownloadListener() {
            @Override
            public void onReset(ITask task) {
                vh.tvStatus.setText(R.string.status_normal);
//                vh.progressBar.setMax(100);
                vh.progressBar.setProgress(0);
                vh.btnStart.bringToFront();
                for (int i = 0; i < tasks.length; i++) {
                    if (tasks[i] == task) {
                        tasks[i] = null;
                        task = null;
                    }
                }
            }

            @Override
            public void onPending(String name, long soFarBytes, long totalBytes) {
                vh.tvTitle.setText(name);
                vh.tvStatus.setText(R.string.status_pending);
                vh.progressBar.setMax((int) (totalBytes / 1000));
                vh.progressBar.setProgress((int) (soFarBytes / 1000));
                vh.btnPause.bringToFront();
            }

            @Override
            public void onStart() {
                vh.tvStatus.setText(R.string.status_started);
            }

            @Override
            public void onConnectSuc(long soFarBytes, long totalBytes) {
                vh.progressBar.setMax((int) (totalBytes / 1000));
                vh.progressBar.setProgress((int) (soFarBytes / 1000));
                vh.tvStatus.setText(R.string.status_connecting);
            }

            @Override
            public void onConnect() {
                vh.tvStatus.setText(R.string.status_connecting);
            }

            @Override
            public void onProgress(long soFarBytes, long totalBytes) {
                vh.tvStatus.setText(R.string.status_downloading);
                vh.progressBar.setMax((int) (totalBytes / 1000));
                vh.progressBar.setProgress((int) (soFarBytes / 1000));
            }

            @Override
            public void onComplete() {
                vh.tvStatus.setText(R.string.status_completed);
                vh.btnDelete.bringToFront();
            }

            @Override
            public void onPause(long soFarBytes, long totalBytes) {
                vh.tvStatus.setText(R.string.status_pause);
                vh.btnStart.bringToFront();
            }

            @Override
            public void onFailure(Throwable e) {
                vh.tvStatus.setText(R.string.status_error);
                vh.btnStart.bringToFront();
            }

            @Override
            public void onRetry(long soFarBytes, long totalBytes) {
                vh.tvStatus.setText(R.string.status_retry);
                vh.progressBar.setMax((int) (totalBytes / 1000));
                vh.progressBar.setProgress((int) (soFarBytes / 1000));
            }
        });
        return task;
    }

    class ListViewAdapter extends BaseAdapter {
        private Context context;
        private String[] urls;

        public ListViewAdapter(Context context, String[] urls) {
            this.urls = urls;
            this.context = context;
            tasks = new ITask[urls.length];
        }

        @Override
        public int getCount() {
            return urls.length;
        }

        @Override
        public Object getItem(int i) {
            return urls[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_view, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ITask task = startTask(urls[position], vh);
                    tasks[position] = task;
                }
            });
            vh.btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tasks[position] != null) {
                        tasks[position].cancel();
                    }
                }
            });
            vh.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tasks[position] != null) {
                        tasks[position].deleteFile();
                    }
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvStatus;
        Button btnStart;
        Button btnPause;
        Button btnDelete;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.tvTitle);
            tvStatus = view.findViewById(R.id.tvStatus);
            btnStart = view.findViewById(R.id.btnStart);
            btnPause = view.findViewById(R.id.btnPause);
            btnDelete = view.findViewById(R.id.btnDelete);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
}
