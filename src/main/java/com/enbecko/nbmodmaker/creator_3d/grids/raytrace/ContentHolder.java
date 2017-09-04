package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.creator_3d.grids.OverlyExtendedBlockStorage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by enbec on 21.02.2017.
 */
public interface ContentHolder <T extends Content>{
    int getContentCount();

    List<T> getContent();

    /**
     * This method is passing down the Content to Add and in which FirstOrderHolder
     * The ContentHolder with the last Child who's not containing the vec is responsible
     * for adding the new Childs.
     * @param chunk
     * @return true if added, false if not.
     */
    boolean addChunk(OverlyExtendedBlockStorage chunk);

    boolean addNewChild(@Nonnull T content);

    boolean removeChild(@Nonnull T content);

    void askForOrderDegrade(@Nonnull T asker, CubicContentHolderGeometry degradeTo);
}
