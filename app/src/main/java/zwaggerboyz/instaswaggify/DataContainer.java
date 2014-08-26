package zwaggerboyz.instaswaggify;

import java.util.List;

import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;

/**
 * Created by zeta on 8/9/14.
 */
public class DataContainer {

    DataType dataType;
    Overlay overlay;
    AbstractFilterClass filter;

    List<Overlay> overlays;
    List<AbstractFilterClass> filters;

    float[] floatValues;

    public enum DataType {
        OVERLAY_DATA,
        FILTER_DATA
    }

    public DataContainer(DataType dataType) {
        this.dataType = dataType;
    }

    public DataContainer(Object data, DataType dataType) {
        this.dataType = dataType;
        if (dataType == DataType.FILTER_DATA) {
            this.filter = (AbstractFilterClass) data;
        }
        else {
            this.overlay = (Overlay) data;
        }
    }

    public DataContainer(Overlay overlay) {
        dataType = DataType.OVERLAY_DATA;
        this.overlay = overlay;
    }

    public DataContainer(AbstractFilterClass filter) {
        dataType = DataType.FILTER_DATA;
        this.filter = filter;
    }

    public DataContainer(List data, DataType dataType) {
        this.dataType = dataType;
        if (dataType == DataType.FILTER_DATA) {
            filters = data;
        }
        else {
            this.overlays = data;
        }
    }

    public DataContainer(float[] data, DataType dataType) {
        this.dataType = dataType;
        floatValues = data;
    }

}
