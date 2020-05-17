package com.sunshine687.shikelang.mappers;
import com.sunshine687.shikelang.pojo.Video;
import com.sunshine687.shikelang.pojo.VideoGroup;
import com.sunshine687.shikelang.pojo.VideoItem;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface MainMapper {
    @Delete("delete from videoItem where videoId = #{id}")
    int deleteVideoItem(Video video);

    @Delete("delete from video where id = #{id}")
    int deleteVideo(Video video);

    @Select("select * from videoGroup")
    ArrayList<VideoGroup> getVideoGroup();

    /**
     * 根据视频组别、视频类别查询所有视频
     * @return 返回视频列表
     */
    @Select("select id,updateUrl,updateTime from video where videoGroupId = #{videoGroupId} and type = #{type}")
    ArrayList<Video> getAllVideo(Integer videoGroupId,String type);

    /**
     * 根据视频id查询下面所有视频剧集
     * @return 返回视频剧集列表
     */
    @Select("select name from videoitem where videoId = #{id}")
    ArrayList<VideoItem> getAllVideoItem(Video video);

    /**
     * 插入单个视频对象（不包含剧集）
     * @param video 单个视频对象
     * @return 成功记录数
     */
    @Insert("insert into video(name,imgUrl,updateStatus,year,area,director,mainPerformer,videoGroupId,type,instruction,createTime,updateTime,updateUrl) values(#{name},#{imgUrl},#{updateStatus},#{year},#{area},#{director},#{mainPerformer},#{videoGroupId},#{type},#{instruction},#{createTime,jdbcType=TIMESTAMP},#{updateTime},#{updateUrl})")
    @Options(useGeneratedKeys=true, keyColumn="id")
    Integer insertVideo(Video video);

    /**
     *
     * @param video 单个视频对象
     * @return 成功记录数
     */
    @Insert("update video set updateStatus = #{updateStatus}, updateTime = #{updateTime} where id = #{id}")
    Integer updateVideo(Video video);

    /**
     * 批量添加视频对象
     * @param result 视频对象集合
     * @return 是否成功
     */
    @Insert("<script> insert into video (name,imgUrl,updateStatus,year,area,director,mainPerformer,videoGroupId,instruction,createTime,updateTime,flag) values  " +
            "  <foreach collection='result' item='item' separator=',' > " +
            "  (#{item.name},#{item.imgUrl},#{item.updateStatus},#{item.year},#{item.area},#{item.director},#{item.mainPerformer},#{item.videoGroupId},#{item.instruction},#{item.createTime,jdbcType=TIMESTAMP},#{item.updateTime},#{item.flag})\n" +
            "  </foreach> </script>")
    Boolean insertVideos(@Param(value = "result") List<Video> result);


    /**
     * 批量添加集数
     * @param result 集数集合
     * @return 是否成功
     */
    @Insert("<script> insert into videoItem (name,url,videoId) values  " +
            "  <foreach collection='result' item='item' separator=',' > " +
            "  (#{item.name},#{item.url},#{item.videoId})\n" +
            "  </foreach> </script>")
    Boolean insertVideoItems(@Param(value = "result") List<VideoItem> result);
}
