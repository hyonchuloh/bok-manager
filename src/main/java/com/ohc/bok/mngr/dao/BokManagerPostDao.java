package com.ohc.bok.mngr.dao;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerPostDto;

public interface BokManagerPostDao {

    public void initTable();
    public int insertItem(BokManagerPostDto input);
    public int updateItem(BokManagerPostDto input);
    public BokManagerPostDto selectItem(int seq);
    public List<BokManagerPostDto> getFeedItems();
    public List<BokManagerPostDto> getThreadItems(int rootSeq);
    public int deleteItem(int seq);
    public int deleteThreadItems(int rootSeq);
    public int likeItem(int seq);

}
