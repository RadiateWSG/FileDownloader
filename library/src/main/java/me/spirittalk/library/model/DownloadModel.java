package me.spirittalk.library.model;

import android.os.Parcel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

import me.spirittalk.library.DownloadStatus;
import me.spirittalk.library.util.FileDownloadUtils;
import me.spirittalk.library.util.FileUtils;

/**
 * Created by spirit on 2017/11/26.
 */
@Entity
public class DownloadModel {
    @Id(autoincrement = true)
    protected Long _id;
    @Unique
    private int id;
    private String url;
    private String path;
    public long sofar;
    private long total;
//    @Convert(converter = DownloadConnectionConvert.class, columnType = String.class)
//    private List<ConnectionModel> connections;
    @Convert(converter = StatusCoverter.class, columnType = Integer.class)
    private DownloadStatus status = DownloadStatus.normal;
    private String etag;
    private String errMsg;

    public static DownloadModel newModel(String url, String dirPath) {
        DownloadModel model = new DownloadModel();
        String fileName = FileDownloadUtils.generateFileName(url);
        model.path = FileDownloadUtils.generateFilePath(dirPath, fileName);
        model.url = url;
        model.id = FileDownloadUtils.generateId(url, model.path);
        return model;
    }

    @Generated(hash = 1181668634)
    public DownloadModel(Long _id, int id, String url, String path, long sofar, long total,
            DownloadStatus status, String etag, String errMsg) {
        this._id = _id;
        this.id = id;
        this.url = url;
        this.path = path;
        this.sofar = sofar;
        this.total = total;
        this.status = status;
        this.etag = etag;
        this.errMsg = errMsg;
    }

    @Generated(hash = 1665448439)
    public DownloadModel() {
    }


    protected DownloadModel(Parcel in) {
        id = in.readInt();
        url = in.readString();
        path = in.readString();
    }

    public String getTempFilePath() {
        return FileUtils.getTempFilePath(path);
    }

    public void reset() {
        setStatus(DownloadStatus.normal);
        setSofar(0);
        setTotal(0);
        setEtag("");
    }

    static class StatusCoverter implements PropertyConverter<DownloadStatus, Integer> {

        @Override
        public DownloadStatus convertToEntityProperty(Integer databaseValue) {
            return DownloadStatus.valueOf(databaseValue);
        }

        @Override
        public Integer convertToDatabaseValue(DownloadStatus entityProperty) {
            return entityProperty.value;
        }
    }

    public int getId() {
        return this.id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getUrl() {
        return this.url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getPath() {
        return this.path;
    }


    public void setPath(String path) {
        this.path = path;
    }


//    public List<ConnectionModel> getConnections() {
//        return this.connections;
//    }
//
//
//    public void setConnections(List<ConnectionModel> connections) {
//        this.connections = connections;
//    }


    public DownloadStatus getStatus() {
        return this.status;
    }


    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public long getSofar() {
        return sofar;
    }

    public void setSofar(long sofar) {
        this.sofar = sofar;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }


    static class DownloadConnectionConvert implements PropertyConverter<List<ConnectionModel>, String> {

        @Override
        public List<ConnectionModel> convertToEntityProperty(String databaseValue) {
            return new Gson().fromJson(databaseValue, new TypeToken<List<ConnectionModel>>() {
            }.getType());
        }

        @Override
        public String convertToDatabaseValue(List<ConnectionModel> entityProperty) {
            return new Gson().toJson(entityProperty);
        }
    }
}
