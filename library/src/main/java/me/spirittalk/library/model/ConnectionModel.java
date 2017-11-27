package me.spirittalk.library.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Locale;

/**
 * Created by spirit on 2017/11/26.
 */
@Entity
public class ConnectionModel implements Parcelable {
    private int id;// id
    private int index;//当前分段位置
    private long startOffset;//开始位置
    private long currentOffset;//当前已下载位置
    private long endOffset;//结束位置


    protected ConnectionModel(Parcel in) {
        id = in.readInt();
        index = in.readInt();
        startOffset = in.readLong();
        currentOffset = in.readLong();
        endOffset = in.readLong();
    }

    @Generated(hash = 875577538)
    public ConnectionModel(int id, int index, long startOffset, long currentOffset,
                           long endOffset) {
        this.id = id;
        this.index = index;
        this.startOffset = startOffset;
        this.currentOffset = currentOffset;
        this.endOffset = endOffset;
    }

    @Generated(hash = 655972545)
    public ConnectionModel() {
    }

    public static final Creator<ConnectionModel> CREATOR = new Creator<ConnectionModel>() {
        @Override
        public ConnectionModel createFromParcel(Parcel in) {
            return new ConnectionModel(in);
        }

        @Override
        public ConnectionModel[] newArray(int size) {
            return new ConnectionModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(index);
        parcel.writeLong(startOffset);
        parcel.writeLong(currentOffset);
        parcel.writeLong(endOffset);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStartOffset() {
        return this.startOffset;
    }

    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }

    public long getCurrentOffset() {
        return this.currentOffset;
    }

    public void setCurrentOffset(long currentOffset) {
        this.currentOffset = currentOffset;
    }

    public long getEndOffset() {
        return this.endOffset;
    }

    public void setEndOffset(long endOffset) {
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "id[%d] index[%d] range[%d, %d) current offset(%d)",
                id, index, startOffset, endOffset, currentOffset);
    }
}
