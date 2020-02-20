package com.sunshine687.shikelang.mappers;
import com.sunshine687.shikelang.pojo.VideoGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface MainMapper {
    @Select(    //记得加空格
            "<script>"
                    + "select "
                    + "* "
                    + "from "
                    + "videoGroup "
                    + "</script>"
    )
    ArrayList<VideoGroup> getVideoGroup();
}
